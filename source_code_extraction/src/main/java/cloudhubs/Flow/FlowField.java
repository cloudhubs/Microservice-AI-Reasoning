package cloudhubs.Flow;

import lombok.Data;

@Data
public class FlowField {
    private MSId msId;

    private String fieldClass;

    private String fieldVariable;

    private ParentMethod parentMethod;

    private Integer line;
}
