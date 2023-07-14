package edu.baylor.ecs.cloudhubs.Flow;

import lombok.Data;

@Data
public class FlowMethodCall {
    private MSId msId;

    private String parentPackageName;

    private String parentClassName;

    private String parentMethodName;

    private String parentClassId;

    private Integer lineNumber;

    private String calledMethodName;

    private String calledServiceId;

    private String statementDeclaration;
}
