#!/usr/bin/env bash
# Captures the Play listing screenshots from a booted emulator.
# Runs inside the reactivecircus/android-emulator-runner step (adb is on PATH,
# emulator is booted, cwd is the repo root). PNGs are written to /tmp/shot-*.png
# and picked up by the workflow's upload-artifact step.
#
# Kept as one committed script (not inline in the workflow) because that action
# executes its `script:` input line-by-line, so shell variables and functions
# would not survive between lines.
set -e

PKG=io.github.muntasimulhaque.ninetynine
APK=app/build/outputs/apk/debug/app-debug.apk

adb install -r "$APK"

# Tap the first UI element whose text contains "$1" (resolution-independent).
tap_text() {
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1 || true
  adb pull /sdcard/ui.xml /tmp/ui.xml >/dev/null 2>&1 || true
  local xy
  xy=$(python3 .github/scripts/tap_text.py "$1" /tmp/ui.xml)
  if [ -n "$xy" ]; then adb shell input tap $xy; fi
}

# 1) Home, light
adb shell am force-stop $PKG
adb shell am start -W -n $PKG/.MainActivity
sleep 5
adb exec-out screencap -p > /tmp/shot-home.png

# 2) Name page. Force-stop first so a FRESH process reads the deep-link extra
# and navigates; re-starting the already-foreground singleTop activity routes
# through onNewIntent, which does not reliably land before the capture.
adb shell am force-stop $PKG
adb shell am start -W -n $PKG/.MainActivity --ei nameNumber 1
sleep 5
adb exec-out screencap -p > /tmp/shot-name.png

# 3) Home, dark
adb shell cmd uimode night yes
adb shell am force-stop $PKG
adb shell am start -W -n $PKG/.MainActivity
sleep 5
adb exec-out screencap -p > /tmp/shot-home-dark.png

# 4) Quiz: Memorize tab -> Quiz (back to light)
adb shell cmd uimode night no
adb shell am force-stop $PKG
adb shell am start -W -n $PKG/.MainActivity
sleep 5
tap_text "Memorize"
sleep 3
tap_text "Quiz"
sleep 5
adb exec-out screencap -p > /tmp/shot-quiz.png