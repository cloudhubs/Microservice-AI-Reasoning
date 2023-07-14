package edu.baylor.ecs.cloudhubs.Flow;

import java.util.List;
import lombok.Data;

@Data
public class FlowRoot {
    private FlowClass msController;

    private FlowMethod msControllerMethod;

    private FlowMethodCall msServiceMethodCall;

    private FlowField msControllerServiceField;

    private FlowClass msService;

    private FlowMethod msServiceMethod;

    private FlowMethodCall msRepositoryMethodCall;

    private FlowField msServiceRepositoryField;

    private FlowClass msRepository;

    private List<RestCall> msRestCalls;

    private FlowMethod msRepositoryMethod;
}
