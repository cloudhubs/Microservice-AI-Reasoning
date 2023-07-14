package edu.baylor.ecs.cloudhubs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Config {
    @JsonProperty(required = true)
    private String rootPath;

    @JsonProperty(required = true)
    private String[] ignoreDirs;

    @JsonProperty(required = true)
    private String crudJsonFile;

    @JsonProperty(required = true)
    private String flowJsonFile;

    @JsonProperty(required = true)
    private String outputFile;
}
