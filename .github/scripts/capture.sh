#!/usr/bin/env bash
# Captures the four Play listing screens (home light, name, home dark, quiz)
# from a booted emulator. Runs inside the reactivecircus/android-emulator-runner
# step (adb on PATH, emulator booted, cwd = repo root).
#
# Arg: a device prefix (phone | 7in | 10in) used for the output filenames, so a
# single workflow can capture all devices and keep the PNGs distinct.
# Writes /tmp/<prefix>-home.png etc. for upload-artifact.
#
# Kept as one committed script (not inline in the workflow) because that action
# executes its `script:` input line-by-line, so functions would not survive.
set -e

PKG=io.github.muntasimulhaque.ninetynine
APK=app/build/outputs/apk/debug/app-debug.apk
PREFIX="${1:-phone}"

adb install -r "$APK"

# Start the app fresh: wake/unlock, force-stop (so deep links are read by a new
# process), give the process a moment to die, launch, wait for the splash.
start_app() {
  adb shell input keyevent KEYCODE_WAKEUP >/dev/null 2>&1 || true
  adb shell wm dismiss-keyguard >/dev/null 2>&1 || true
  adb shell am force-stop $PKG
  sleep 2
  adb shell am start -W -n $PKG/.MainActivity
  sleep 10
}

# Capture a frame, retrying until it is not a blank solid colour (i.e. until the
# app has actually drawn content, not still the splash). Needs Pillow on the host.
capture() {
  local name="$1"
  for _ in 1 2 3 4 5; do
    adb exec-out screencap -p > "/tmp/${PREFIX}-${name}.png"
    if python3 -c "
import sys
from PIL import Image
im = Image.open(sys.argv[1]).convert('RGB')
sys.exit(0 if len(im.getcolors(1000000) or []) > 1 else 1)
" "/tmp/${PREFIX}-${name}.png"; then
      return 0
    fi
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
capture home

# 2) Name page: tap a name in the warm home list (reliable; a cold-start deep
# link on the CI emulator landed on a blank sheet).
tap_text "Al-Ahad"
sleep 5
capture name

# back to home
adb shell input keyevent KEYCODE_BACK
sleep 3

# 3) Home, dark: flip night mode in place; the activity recomposes to dark.
adb shell cmd uimode night yes
sleep 6
capture home-dark

# back to light for the quiz
adb shell cmd uimode night no
sleep 3

# 4) Quiz: Memorize tab -> Quiz
tap_text "Memorize"
sleep 3
tap_text "Quiz"
sleep 5
capture quiz