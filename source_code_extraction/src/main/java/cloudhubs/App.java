package cloudhubs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import cloudhubs.services.CRUDService;
import cloudhubs.services.FlowService;

/**
 * Hello world!
 */
public final class App {
    private App() {
    }

    static String modifierToString(NodeList<Modifier> modifiers) {
        return modifiers.toString().replaceFirst("^\\[", "").replaceFirst(" ?]$", "");
    }

    static String getClassRole(ClassOrInterfaceDeclaration c) {
        if (c.getAnnotations().stream().anyMatch(a -> a.getNameAsString().equals("RestController")
                || a.getNameAsString().equals("Controller"))) {
            return "controller";
        }
        if (c.getAnnotations().stream().anyMatch(a -> a.getNameAsString().equals("Service"))) {
            return "service";
        }
        if (c.getAnnotations().stream().anyMatch(a -> a.getNameAsString().equals("Repository"))) {
            return "repository";
        }

        return "other";
    }

    static String getClassDeclarationType(ClassOrInterfaceDeclaration c) {
        if (c.isInterface()) {
            return "interface";
        }
        if (c.isEnumDeclaration()) {
            return "enum";
        }
        if (c.isAnnotationDeclaration()) {
            return "annotation";
        }

        return "class";
    }

    static boolean isGetMethod(MethodDeclaration method) {
        return method.getParameters().isEmpty() && !Objects.equals(method.getType().asString(), "void")
                && (method.getNameAsString().startsWith("get")
                        || (method.getNameAsString().startsWith("is")
                                && Objects.equals(method.getType().asString(), "boolean")));
    }

    static boolean isSetMethod(MethodDeclaration method) {
        return method.getParameters().size() == 1 && Objects.equals(method.getType().asString(), "void")
                && method.getNameAsString().startsWith("set");
    }

    static void fillCrud(List<MethodOutput> outputs, String filePath) throws IOException {
        var crudService = new CRUDService(filePath);
        crudService.loadCrudData();
        var objectMapper = new ObjectMapper();

        for (var output : outputs) {
            var crud = crudService.getCrudByMethod(output);
            output.setCrud(objectMapper.writeValueAsString(crud));
        }
    }

    static void fillFlow(List<MethodOutput> outputs, String filePath) throws IOException {
        var flowService = new FlowService(filePath);
        flowService.loadFlowData();

        var objectMapper = new ObjectMapper();

        for (var output : outputs) {
            var flow = flowService.getFlowByMethod(output);
            output.setFlow(objectMapper.writeValueAsString(flow));
        }
    }

    static String getMicroserviceName(File sourceFile) {
        String microserviceName = null;
        while (sourceFile.getParentFile() != null) {
            var parentFile = sourceFile.getParentFile();
            var pomFile = Arrays.stream(parentFile.listFiles(x -> x.getName().equals("pom.xml"))).findFirst()
                    .orElse(null);
            if (pomFile != null) {
                try {
                    var pomFileContent = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pomFile);
                    var possibleNames = pomFileContent.getElementsByTagName("artifactId");
                    for (int i = 0; i < possibleNames.getLength(); i++) {
                        var possibleName = possibleNames.item(i);
                        if (possibleName.getParentNode().getNodeName() == "project" && possibleName.getParentNode()
                                .getParentNode() == possibleName.getParentNode().getOwnerDocument()) {
                            microserviceName = possibleName.getTextContent();
                            break;
                        }
                    }
                    } catch (SAXException | IOException | ParserConfigurationException e) {
                    System.err.println("Error parsing pom.xml, microservice name cannot be detected");
                    System.err.println(e.getMessage());
                }
                break;
            }
            sourceFile = sourceFile.getParentFile();
        }

        return microserviceName;
    }

    /**
     * Says hello to the world.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) throws IOException {
        var configPath = "config.json";
        if (args.length == 0) {
            System.out.println("Configuration file not provided. Using default `config.json`");
        } else {
            configPath = args[0];
        }
        var configFile = FileUtils.getFile(configPath);
        if (!configFile.exists()) {
            System.out.println("Configuration file `" + configPath
                    + "` not found. See samples/config.json for an example.");
            return;
        }
        var config = new ObjectMapper().readValue(configFile, Config.class);
        var rootDir = new File(config.getRootPath());
        var ignoreDirs = config.getIgnoreDirs() == null ? new String[0] : config.getIgnoreDirs();

        var javaFiles = FileUtils.listFiles(rootDir, new String[] {"java"}, true);
        javaFiles.removeIf(f -> {
            for (var ignoreDir : ignoreDirs) {
                if (f.getPath().contains(rootDir.getPath() + File.separator + ignoreDir)) {
                    return true;
                }
            }
            return false;
        });

        var outputs = new ArrayList<MethodOutput>();

        for (var file : javaFiles) {
            var compilationUnit = StaticJavaParser.parse(file);
            var packageDeclaration = compilationUnit.findFirst(PackageDeclaration.class);
            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(c -> {
                var classModifiers = modifierToString(c.getModifiers());
                var className = c.getNameAsString();
                var classType = getClassRole(c);
                var classDeclarationType = getClassDeclarationType(c);
                var filePath = file.getPath().replace(rootDir.getPath(), "").replace("\\", "/")
                // .replaceFirst("^/", "")
                ;
                var microserviceName = getMicroserviceName(file);

                for (var method : c.getMethods().stream().filter(
                        m -> !isGetMethod(m) && !isSetMethod(m) && !m.isConstructorDeclaration())
                        .toList()) {
                    var methodModifiers = modifierToString(method.getModifiers());
                    var methodName = method.getNameAsString();
                    var methodSourceCode = method.toString();

                    var output = new MethodOutput();
                    output.setClassName(className);
                    output.setPackageName(packageDeclaration.get().getNameAsString());
                    output.setClassComponentType(classType);
                    output.setClassDeclarationType(classDeclarationType);
                    output.setClassFilePath(filePath);
                    output.setClassModifiers(classModifiers);
                    output.setMethodName(methodName);
                    output.setMethodModifiers(methodModifiers);
                    output.setMethodSourceCode(methodSourceCode);
                    output.setMethodOffset(
                            method.getTokenRange().get().getBegin().getRange().get().begin
                                    .toString());
                    output.setMicroserviceName(microserviceName);

                    outputs.add(output);
                }
            });
        }

        fillCrud(outputs, config.getCrudJsonFile());
        fillFlow(outputs, config.getFlowJsonFile());

        System.out.println(outputs.size());
        var mapper = new CsvMapper();
        var schema = mapper.schemaFor(MethodOutput.class).withHeader();
        var writer = mapper.writerFor(outputs.getClass()).with(schema);
        var outputContent = writer.writeValueAsString(outputs);
        var outputFile = new File(config.getOutputFile());
        FileUtils.writeStringToFile(outputFile, outputContent, "UTF-8");
    }
}
