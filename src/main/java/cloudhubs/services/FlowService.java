package cloudhubs.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cloudhubs.MethodOutput;
import cloudhubs.Flow.FlowClass;
import cloudhubs.Flow.FlowField;
import cloudhubs.Flow.FlowMethod;
import cloudhubs.Flow.FlowMethodCall;
import cloudhubs.Flow.RestCall;

public class FlowService {
    private Map<String, List<SimpleEntry<String, Object>>> flowData;

    private String filePath;

    public FlowService(String filePath) {
        this.filePath = filePath;
    }

    public void loadFlowData() throws IOException {
        flowData = new HashMap<String, List<SimpleEntry<String, Object>>>();
        var mapper = new ObjectMapper();
        var tree = mapper.readTree(new File(filePath));
        for (var node : tree) {
            var fieldsIterator = node.fields();
            List<SimpleEntry<String, Object>> values = new ArrayList<>();
            while (fieldsIterator.hasNext()) {
                var field = fieldsIterator.next();
                Object val = null;
                switch (field.getKey()) {
                    case "msController":
                        val = mapper.readValue(field.getValue().toString(), FlowClass.class);
                        break;
                    case "msControllerMethod":
                        val = mapper.readValue(field.getValue().toString(), FlowMethod.class);
                        break;
                    case "msServiceMethodCall":
                        val = mapper.readValue(field.getValue().toString(), FlowMethodCall.class);
                        break;
                    case "msControllerServiceField":
                        val = mapper.readValue(field.getValue().toString(), FlowField.class);
                        break;
                    case "msService":
                        val = mapper.readValue(field.getValue().toString(), FlowClass.class);
                        break;
                    case "msServiceMethod":
                        val = mapper.readValue(field.getValue().toString(), FlowMethod.class);
                        break;
                    case "msRepositoryMethodCall":
                        val = mapper.readValue(field.getValue().toString(), FlowMethodCall.class);
                        break;
                    case "msServiceRepositoryField":
                        val = mapper.readValue(field.getValue().toString(), FlowField.class);
                        break;
                    case "msRepository":
                        val = mapper.readValue(field.getValue().toString(), FlowClass.class);
                        break;
                    case "msRestCalls":
                        val = mapper.readValue(field.getValue().toString(),
                                new TypeReference<List<RestCall>>() {});
                        break;
                    case "msRepositoryMethod":
                        val = mapper.readValue(field.getValue().toString(), FlowMethod.class);
                        break;
                }
                values.add(new SimpleEntry<String, Object>(field.getKey(), val));
            }

            for (int i = 0; i < values.size(); i++) {
                if (values.get(i).getValue() instanceof FlowMethod) {
                    var flowMethod = (FlowMethod) values.get(i).getValue();
                    var prev = i > 0 ? values.get(i - 1).getValue() : null;
                    var initIndex = i;
                    if (prev != null && prev instanceof FlowClass
                            && ((FlowClass) prev).getClassId() == flowMethod.getClassId()) {
                        initIndex--;
                    }
                    var flowFromClass = values.subList(initIndex, values.size());

                    flowData.put(flowMethod.getMethodId(), flowFromClass);
                }
            }
        }
    }

    public List<SimpleEntry<String, Object>> getFlowByMethod(MethodOutput methodOutput) {
        var key = methodOutput.getSymbolId();

        return flowData.getOrDefault(key, List.of());
    }
}
