package edu.baylor.ecs.cloudhubs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CRUD {
    @JsonProperty("CREATE")
    private Boolean create;

    @JsonProperty("READ")
    private Boolean read;

    @JsonProperty("UPDATE")
    private Boolean update;

    @JsonProperty("DELETE")
    private Boolean delete;
}
