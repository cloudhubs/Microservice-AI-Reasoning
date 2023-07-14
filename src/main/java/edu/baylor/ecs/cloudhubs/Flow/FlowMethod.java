package edu.baylor.ecs.cloudhubs.Flow;

import java.util.List;
import lombok.Data;

@Data
public class FlowMethod {
    private MSId msId;

    private String returnType;

    private String methodName;

    private String className;

    private String packageName;

    private String methodId;

    private String classId;

    private Integer line;

    private List<Argument> msArgumentList;

    private List<Annotation> msAnnotations;
}
