import json

import fire

from call_tree.describe import DescribeFlowVisitorV1
from call_tree.parse import parse_flow_v1


def main(flow_file: str, output_file: str):
    with open(flow_file, "r") as f:
        flow_json = json.load(f)
    flow = parse_flow_v1(flow_json)
    visitor = DescribeFlowVisitorV1()
    for node in flow:
        visitor.visit(node)

    with open(output_file, "w") as f:
        f.write(visitor.message)


if __name__ == "__main__":
    fire.Fire(main)
