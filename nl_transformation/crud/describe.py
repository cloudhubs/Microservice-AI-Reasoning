from utils.visitor import *
from typing import Tuple, Dict


def describe_crud(crud_dict: Dict[str, Dict[str, bool]]) -> str:
    messages = ["CRUD operations are performed over the following entities:"]
    indent = "    "
    for entity, crud_dict in crud_dict.items():
        messages.append(
            f"{indent}{entity}: {', '.join([key for key in crud_dict if crud_dict[key]])}"
        )
    return "\n".join(messages)
