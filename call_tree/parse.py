from call_tree.flow_types import *

MAP_FLOW_TYPE = {
    "msController": MsController,
    "msService": MsService,
    "msRepository": MsRepository,
    "msControllerMethod": MsControllerMethod,
    "msServiceMethod": MsServiceMethod,
    "msRepositoryMethod": MsRepositoryMethod,
    "msServiceMethodCall": MsServiceMethodCall,
    "msRepositoryMethodCall": MsRepositoryMethodCall,
    "msControllerServiceField": MsControllerServiceField,
    "msServiceRepositoryField": MsServiceRepositoryField,
    "msRestCalls": MsRestCalls,
}


def parse_flow_v1(flow_json: dict):
    flow = []
    for key, value in flow_json.items():
        flow.append(MAP_FLOW_TYPE[key].from_json(value))
    return flow
