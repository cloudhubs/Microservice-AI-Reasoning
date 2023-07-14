package edu.baylor.ecs.cloudhubs.Flow;

import lombok.Data;

@Data
public class Annotation {
    private Boolean isHttpAnnotation;

    private String annotationName;

    private String key;

    private String value;
}
