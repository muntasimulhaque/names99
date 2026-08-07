#!/usr/bin/env bash
# Captures the Play listing screenshots from a booted emulator.
# Runs inside the reactivecircus/android-emulator-runner step (adb on PATH,
# emulator booted, cwd = repo root). Writes /tmp/shot-*.png for upload-artifact.
#
# Kept as one committed script (not inline in the workflow) because that action
# executes its `script:` input line-by-line, so functions/variables would not
# survive between lines.
set -e

PKG=io.github.muntasimulhaque.ninetynine
APK=app/build/outputs/apk/debug/app-debug.apk

adb install -r "$APK"

# Start the app fresh: force-stop (so the deep-link extra is read by a new
# process and the singleTop onNewIntent path is avoided), give the process a
# moment to die, launch, then wait for the splash to clear before returning.
start_app() {
  adb shell am force-stop $PKG
  sleep 2
  adb shell am start -W -n $PKG/.MainActivity "$@"
  sleep 10
}

# Capture a frame, retrying until it is not a blank solid colour. A blank
# splash/loading PNG compresses to ~15 KB; once content is drawn the file grows,
# so a size floor is a cheap "did the UI render yet" signal.
capture() {
  local out="$1"
  for _ in 1 2 3 4; do
    adb exec-out screencap -p > "$out"
    local sz
    sz=$(wc -c < "$out")
    if [ "$sz" -gt 16000 ]; then return 0; fi
    sleep 6
  done
}

# Tap the first UI element whose text contains "$1" (resolution-independent).
tap_text() {
  adb shell uiautomator dump /sdcard/ui.xml >/dev/null 2>&1 || true
  adb pull /sdcard/ui.xml /tmp/ui.xml >/dev/null 2>&1 || true
  local xy
  xy=$(python3 .github/scripts/tap_text.py "$1" /tmp/ui.xml)
  if [ -n "$xy" ]; then adb shell input tap $xy; fi
}

# 1) Home, light
start_app
capture /tmp/shot-home.png

# 2) Name page (deep-link to the first name)
start_app --ei nameNumber 1
capture /tmp/shot-name.png

# 3) Home, dark
adb shell cmd uimode night yes
start_app
capture /tmp/shot-home-dark.png

# 4) Quiz: Memorize tab -> Quiz (back to light)
adb shell cmd uimode night no
start_app
tap_text "Memorize"
sleep 3
tap_text "Quiz"
sleep 5
capture /tmp/shot-quiz.png