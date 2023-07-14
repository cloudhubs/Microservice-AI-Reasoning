package edu.baylor.ecs.cloudhubs;

import lombok.Data;

@Data
public class MethodOutput {
    public String className;

    public String packageName;

    // controller, service, repository, other
    public String classRole;

    // interface, enum, annotation, class
    public String classDeclarationType;

    public String classFilePath;

    // public, private, protected, static, final, abstract. Multiple modifiers are separated by
    // comma
    public String classModifiers;

    public String methodName;

    // similar as classModifiers
    public String methodModifiers;

    public String methodSourceCode;

    // The position in the file where the method starts, in the form of (line <l>,col <c>)
    public String methodStartSourceCode;

    public String getSymbolId() {
        return this.packageName + "." + this.className + "." + this.methodName;
    }

    public String crud;

    public String flow;
}
