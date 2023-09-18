import csv
import json
from functools import reduce

import fire

from crud.describe import describe_crud
from flows.describe import DescribeFlowVisitorV1
from flows.parse import parse_flow_v1


def main(csv_file: str, output_file: str):
    with open(csv_file, "r", encoding="utf-8") as csv_file:
        with open(output_file, "w", encoding="utf-8") as f_out:
            reader = csv.DictReader(csv_file)
            writer = csv.DictWriter(
                f_out, reader.fieldnames + ["flowCrudDescription"], lineterminator="\n"
            )
            writer.writeheader()

            for row in reader:
                class_role, crud, flow = row["classRole"], row["crud"], row["flow"]
                if class_role == "controller":
                    # Flow
                    flow_message = "There is no flow information available."
                    if flow:
                        flow_json_list = json.loads(flow)
                        flow_json = reduce(lambda x, y: {**x, **y}, flow_json_list, {})
                        flow = parse_flow_v1(flow_json)
                        visitor = DescribeFlowVisitorV1()
                        for node in flow:
                            visitor.visit(node)
                        flow_message = visitor.message

                    # CRUD
                    crud_message = "No CRUD operations performed."
                    if crud:
                        crud_dict = json.loads(crud)
                        if crud_dict:
                            crud_message = f"Additionally, {describe_crud(crud_dict)}"

                    row["flowCrudDescription"] = flow_message + "\n" + crud_message
                writer.writerow(row)


if __name__ == "__main__":
    fire.Fire(main)
