package edu.baylor.ecs.cloudhubs.Flow;

import lombok.Data;

@Data
public class RestCall {
    private String api;

    private String httpMethod;

    private String returnType;

    private MSId msId;

    private String parentPackageName;

    private String parentClassName;

    private String parentMethodName;

    private String parentClassId;

    private Integer lineNumber;

    private String statementDeclaration;
}
