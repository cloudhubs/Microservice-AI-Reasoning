from utils.visitor import *
from flows.flow_types import *


class DescribeFlowVisitorV1:
    def __init__(self):
        self._context = {}
        self._messages = []

    @property
    def message(self):
        return "\n".join(self._messages)

    @on("node")
    def visit(self, node: FlowMSObject):
        pass

    # === Class level ===

    @when(MsController)
    def visit(self, node: MsController):
        self._context["controller"] = node

    @when(MsService)
    def visit(self, node: MsService):
        self._context["service"] = node

    @when(MsRepository)
    def visit(self, node: MsRepository):
        self._context["repository"] = node

    # === Method level ===

    @when(MsControllerMethod)
    def visit(self, node: MsControllerMethod):
        if "controller" not in self._context:
            self._context["controller"] = node.get_parent_class_copy()

        controller_name = self._context["controller"].class_name
        controller_method = node.method_name
        http_method_type = node.find_http_type()
        return_type = node.return_type

        assert http_method_type is not None, "No HTTP method type found"
        self._messages.append(
            f"The '{controller_name}' has a '{http_method_type}' endpoint handled by the method '{controller_method}'. The endpoint returns an '{return_type}' object."
        )

        self._context["controller_method"] = node

    @when(MsServiceMethod)
    def visit(self, node: MsServiceMethod):
        controller_method = self._context["controller_method"].method_name
        controller_name = self._context["controller"].class_name
        service_method = node.method_name
        service_name = self._context["controller_service_field"].field_class
        service_field = self._context["controller_service_field"].field_variable
        return_type = node.return_type

        self._messages.append(
            f"The '{controller_method}' method in the '{controller_name}' controller calls the method '{service_method}' of the '{service_name}' service through its field '{service_field}'. This calls returns a '{return_type}' object."
        )

        self._context["service_method"] = node

    @when(MsRepositoryMethod)
    def visit(self, node: MsRepositoryMethod):
        service_method = self._context["service_method"].method_name
        service_name = self._context["controller_service_field"].field_class
        repository_method = node.method_name
        repository_name = self._context["service_repository_field"].field_class
        repository_field = self._context["service_repository_field"].field_variable
        return_type = node.return_type

        self._messages.append(
            f"The '{service_method}' method in the '{service_name}' service calls the method '{repository_method}' of the '{repository_name}' respository through its field '{repository_field}'. This call returns a '{return_type}' object."
        )

    # === Calls level ===

    @when(MsRestCalls)
    def visit(self, node: MsRestCalls):
        if len(node.calls) == 0:
            return

        rest_calls_msgs = []
        for rest_call in node.calls:
            target_endpoint = rest_call.api
            endpoint_message = (
                f"an undetermined"
                if target_endpoint is None
                else f"the '{target_endpoint}'"
            )
            return_type = rest_call.return_type
            rest_calls_msgs.append(
                f"    - It communicates with {endpoint_message} endpoint. This request expects '{return_type}' object as response."
            )

        service_method = self._context["service_method"].method_name
        service_name = self._context["controller_service_field"].field_class

        self._messages.append(
            f"The '{service_method}' method in the '{service_name}' communicates with other microservices in the following ways:\n"
            + "\n".join(rest_calls_msgs)
        )

    @when(MsMethodCall)
    def visit(self, node: MsMethodCall):
        # method call objects are redundant, don't provide any additional information
        pass

    # === Field level ===

    @when(MsControllerServiceField)
    def visit(self, node: MsControllerServiceField):
        self._context["controller_service_field"] = node

    @when(MsServiceRepositoryField)
    def visit(self, node: MsServiceRepositoryField):
        self._context["service_repository_field"] = node
