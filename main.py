from call_tree.parse import parse_flow_v1
from call_tree.describe import DescribeFlowVisitorV1

if __name__ == "__main__":
    import json

    with open("test/flow1.json") as f:
        flow1_json = json.load(f)

    with open("test/flow2.json") as f:
        flow2_json = json.load(f)

    flow1 = parse_flow_v1(flow1_json)
    flow2 = parse_flow_v1(flow2_json)

    describe_visitor = DescribeFlowVisitorV1()
    for node in flow1:
        describe_visitor.visit(node)

    print(describe_visitor.message)
