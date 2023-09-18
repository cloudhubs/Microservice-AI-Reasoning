package cloudhubs.services;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import cloudhubs.CRUD;
import cloudhubs.MethodOutput;

public class CRUDService {
    private Map<String, Map<String, CRUD>> crudData;

    private String filePath;

    public CRUDService(String filePath) {
        this.filePath = filePath;
    }

    public void loadCrudData() throws IOException {
        crudData = new ObjectMapper().readValue(new File(filePath),
                new TypeReference<Map<String, Map<String, CRUD>>>() {});
    }

    public Map<String, CRUD> getCrudByMethod(MethodOutput methodOutput) {
        var key = methodOutput.getPackageName() + "." + methodOutput.getClassName() + "#"
                + methodOutput.getMethodName();

        return crudData.getOrDefault(key, Map.of());
    }
}
