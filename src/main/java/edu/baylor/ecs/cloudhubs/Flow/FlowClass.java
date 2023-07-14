package edu.baylor.ecs.cloudhubs.Flow;

import lombok.Data;

@Data
public class FlowClass {
    private MSId msId;

    private String classId;

    private String packageName;

    private String className;

    private String role;
}
