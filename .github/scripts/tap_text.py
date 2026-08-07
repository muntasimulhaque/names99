#!/usr/bin/env python3
"""Find the pixel center of a UI element by its text, for the screenshot workflow.

Reads a `uiautomator dump` XML and prints "x y" for the first node whose text
contains the target, so the workflow can `adb shell input tap` it without
hard-coding screen coordinates (which differ by emulator resolution).

Usage: tap_text.py "<target text>" [path-to-ui.xml]
Prints nothing (exit 0) when no node matches.
"""
import re
import sys


def main() -> None:
    target = sys.argv[1].lower()
    dump = sys.argv[2] if len(sys.argv) > 2 else "/tmp/ui.xml"
    try:
        xml = open(dump, encoding="utf-8", errors="ignore").read()
    except Exception:
        return
    for node in re.finditer(r"<node\b[^>]*>", xml):
        block = node.group(0)
        text = re.search(r'text="([^"]*)"', block)
        bounds = re.search(r'bounds="\[(\d+),(\d+)\]\[(\d+),(\d+)\]"', block)
        if text and bounds and target in text.group(1).lower():
            x1, y1, x2, y2 = map(int, bounds.groups())
            print(f"{(x1 + x2) // 2} {(y1 + y2) // 2}")
            return


if __name__ == "__main__":
    main()