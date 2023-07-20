from typing import List


class FlowMSObject:
    pass


class MsId(FlowMSObject):
    def __init__(self, path: str, directory_name: str):
        self.path = path
        self.directory_name = directory_name

    @staticmethod
    def from_json(json_dict: dict):
        return MsId(json_dict["path"], json_dict["directoryName"])


class MsClass(FlowMSObject):
    def __init__(self, ms_id: MsId, class_id: str, package_name: str, class_name: str):
        self.ms_id = ms_id
        self.class_id = class_id
        self.package_name = package_name
        self.class_name = class_name

    @property
    def role(self):
        raise NotImplementedError()

    @classmethod
    def from_json(cls, json_dict: dict):
        return cls(
            MsId.from_json(json_dict["msId"]),
            json_dict["classId"],
            json_dict["packageName"],
            json_dict["className"],
        )


class MsController(MsClass):
    @property
    def role(self):
        return "CONTROLLER"


class MsService(MsClass):
    @property
    def role(self):
        return "SERVICE"


class MsRepository(MsClass):
    @property
    def role(self):
        return "REPOSITORY"


class MsArgument(FlowMSObject):
    def __init__(self, return_type: str):
        self.return_type = return_type

    @staticmethod
    def from_json(json_dict: dict):
        return MsArgument(json_dict["returnType"])


class MsAnnotation(FlowMSObject):
    def __init__(
        self,
        is_http_annotation: bool,
        annotation_name: str,
        key: str = None,
        value: str = None,
    ):
        self.is_http_annotation = is_http_annotation
        self.annotation_name = annotation_name
        self.key = key
        self.value = value

    @staticmethod
    def from_json(json_dict: dict):
        return MsAnnotation(
            json_dict["isHttpAnnotation"],
            json_dict["annotationName"],
            json_dict.get("key"),
            json_dict.get("value"),
        )


class MsMethod(FlowMSObject):
    def __init__(
        self,
        ms_id: MsId,
        return_type: str,
        method_name: str,
        class_name: str,
        package_name: str,
        method_id: str,
        class_id: str,
        line: int,
        argument_list: List[MsArgument],
        annotations: List[MsAnnotation],
    ):
        self.ms_id = ms_id
        self.return_type = return_type
        self.method_name = method_name
        self.class_name = class_name
        self.package_name = package_name
        self.method_id = method_id
        self.class_id = class_id
        self.line = line
        self.argument_list = argument_list
        self.annotations = annotations

    @classmethod
    def from_json(cls, json_dict: dict):
        return cls(
            MsId.from_json(json_dict["msId"]),
            json_dict["returnType"],
            json_dict["methodName"],
            json_dict["className"],
            json_dict["packageName"],
            json_dict["methodId"],
            json_dict["classId"],
            json_dict["line"],
            [MsArgument.from_json(arg) for arg in json_dict["msArgumentList"]],
            [MsAnnotation.from_json(ann) for ann in json_dict["msAnnotations"]],
        )


class MsControllerMethod(MsMethod):
    pass


class MsServiceMethod(MsMethod):
    pass


class MsRepositoryMethod(MsMethod):
    pass


class MsMethodCall(FlowMSObject):
    def __init__(
        self,
        ms_id: MsId,
        parent_package_name: str,
        parent_class_name: str,
        parent_method_name: str,
        parent_class_id: str,
        line_number: int,
        called_method_name: str,
        called_service_id: str,
        statement_declaration: str,
    ):
        self.ms_id = ms_id
        self.parent_package_name = parent_package_name
        self.parent_class_name = parent_class_name
        self.parent_method_name = parent_method_name
        self.parent_class_id = parent_class_id
        self.line_number = line_number
        self.called_method_name = called_method_name
        self.called_service_id = called_service_id
        self.statement_declaration = statement_declaration

    @classmethod
    def from_json(cls, json_dict: dict):
        return cls(
            MsId.from_json(json_dict["msId"]),
            json_dict["parentPackageName"],
            json_dict["parentClassName"],
            json_dict["parentMethodName"],
            json_dict["parentClassId"],
            json_dict["lineNumber"],
            json_dict["calledMethodName"],
            json_dict["calledServiceId"],
            json_dict["statementDeclaration"],
        )


class MsServiceMethodCall(MsMethodCall):
    pass


class MsRepositoryMethodCall(MsMethodCall):
    pass


class MsField(FlowMSObject):
    def __init__(
        self,
        ms_id: MsId,
        field_class: str,
        field_variable: str,
        parent_class_name: str,
        parent_package_name: str,
        line: int,
    ):
        self.ms_id = ms_id
        self.field_class = field_class
        self.field_variable = field_variable
        self.parent_class_name = parent_class_name
        self.parent_package_name = parent_package_name
        self.line = line

    @classmethod
    def from_json(cls, json_dict: dict):
        return cls(
            MsId.from_json(json_dict["msId"]),
            json_dict["fieldClass"],
            json_dict["fieldVariable"],
            json_dict["parentMethod"]["parentClassName"],
            json_dict["parentMethod"]["parentPackageName"],
            json_dict["line"],
        )


class MsControllerServiceField(MsField):
    pass


class MsServiceRepositoryField(MsField):
    pass


class MsRestCall(FlowMSObject):
    def __init__(
        self,
        ms_id: MsId,
        api: str,
        htt_method: str,
        return_type: str,
        parent_package_name: str,
        parent_class_name: str,
        parent_method_name: str,
        parent_class_id: str,
        line_number: int,
        statement_declaration: str,
    ):
        self.ms_id = ms_id
        self.api = api
        self.htt_method = htt_method
        self.return_type = return_type
        self.parent_package_name = parent_package_name
        self.parent_class_name = parent_class_name
        self.parent_method_name = parent_method_name
        self.parent_class_id = parent_class_id
        self.line_number = line_number
        self.statement_declaration = statement_declaration

    @staticmethod
    def from_json(json_dict: dict):
        return MsRestCall(
            MsId.from_json(json_dict["msId"]),
            json_dict.get("api"),
            json_dict["httpMethod"],
            json_dict["returnType"],
            json_dict["parentPackageName"],
            json_dict["parentClassName"],
            json_dict["parentMethodName"],
            json_dict["parentClassId"],
            json_dict["lineNumber"],
            json_dict["statementDeclaration"],
        )


class MsRestCalls(FlowMSObject):
    def __init__(self, calls: List[MsRestCall]):
        self.calls = calls

    @staticmethod
    def from_json(json_dict: dict):
        return MsRestCalls([MsRestCall.from_json(call) for call in json_dict])
