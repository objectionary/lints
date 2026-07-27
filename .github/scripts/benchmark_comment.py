#!/usr/bin/env python3
from __future__ import annotations

import csv
import math
import re
import sys
from collections import defaultdict
from pathlib import Path


def read_timings(path: Path) -> dict[str, float]:
    totals: dict[str, list[float]] = defaultdict(list)
    with path.open(newline="", encoding="utf-8") as handle:
        for row in csv.DictReader(handle):
            name = row["id"].strip().strip('"')
            if not name:
                continue
            lint = re.sub(r"\s+\([^)]+\)$", "", name)
            totals[lint].append(float(row["ms"].strip().strip('"')))
    return {lint: sum(values) / len(values) for lint, values in totals.items()}


def fmt(value: float | None) -> str:
    return "n/a" if value is None else f"{value:.1f}"


def pct(base: float | None, head: float | None) -> str:
    if base in (None, 0) or head is None:
        return "n/a"
    return f"{((head - base) / base) * 100:.1f}%"


def main() -> int:
    if len(sys.argv) != 3:
        print("usage: benchmark_comment.py BASE.csv HEAD.csv", file=sys.stderr)
        return 1
    base = read_timings(Path(sys.argv[1]))
    head = read_timings(Path(sys.argv[2]))
    rows = []
    for lint in sorted(set(base) | set(head)):
        b = base.get(lint)
        h = head.get(lint)
        delta = math.inf if b is None or h is None else h - b
        rows.append((delta, lint, b, h))
    rows.sort(key=lambda item: (math.isinf(item[0]), item[0], item[1]), reverse=True)
    print("## Benchmark results")
    print()
    print("| lint | base ms | head ms | delta |")
    print("| --- | ---: | ---: | ---: |")
    for delta, lint, b, h in rows[:16]:
        change = "n/a" if math.isinf(delta) else f"{delta:+.1f}"
        print(f"| `{lint}` | {fmt(b)} | {fmt(h)} | {change} ({pct(b, h)}) |")
    print()
    print("Generated from `target/timings.csv` on base and head commits.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
