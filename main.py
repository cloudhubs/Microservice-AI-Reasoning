import json
import sys
from pathlib import Path

import tiktoken

enc = tiktoken.get_encoding("cl100k_base")


def get_files_by_extension(
    root_path: Path, ignore_dirs: list[Path]
) -> dict[str, list[Path]]:
    files = {}
    for file in root_path.glob(f"**/*"):
        if file.is_dir():
            continue
        if any(str(file).startswith(str(ignore_dir)) for ignore_dir in ignore_dirs):
            continue
        files[file.suffix] = files.get(file.suffix, []) + [file]
    return files


def get_number_of_tokens(files: list[Path]) -> dict[int, int]:
    token_counts = 0
    for file in files:
        tokens = enc.encode(file.read_text(encoding="utf-8"))
        token_counts += len(tokens)

    return token_counts


if __name__ == "__main__":
    config_file = Path("config.json")
    if len(sys.argv) < 2 and not config_file.exists():
        print("No config file provided and default config.json not found.")
        print("Usage: python main.py <config_file>")
        sys.exit(1)
    if len(sys.argv) > 1:
        config_file = Path(sys.argv[1])

    config = json.loads(config_file.read_text())
    root_path = Path(config["rootPath"])
    ignore_dirs = [root_path / ignore_dir for ignore_dir in config["ignoreDirs"]]

    files_by_ext = get_files_by_extension(root_path, ignore_dirs)

    number_of_tokens = get_number_of_tokens(files_by_ext[".java"])

    print(f"Number of tokens: {number_of_tokens}")
