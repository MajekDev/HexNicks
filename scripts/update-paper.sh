#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
RUN_DIR="$PROJECT_ROOT/run"
TIMESTAMP_FILE="$RUN_DIR/.paper-last-checked"

VERSION="26.1.2"
USER_AGENT="HexNicks-dev/3.2.1 (majekdor@gmail.com)"
CHECK_INTERVAL_SECONDS=$((7 * 24 * 60 * 60))  # 7 days

mkdir -p "$RUN_DIR"

# Skip the check entirely if we already have a jar and checked recently
if [ -f "$RUN_DIR/paper-latest.jar" ] && [ -f "$TIMESTAMP_FILE" ]; then
  LAST_CHECKED=$(cat "$TIMESTAMP_FILE")
  NOW=$(date +%s)
  ELAPSED=$((NOW - LAST_CHECKED))

  if [ "$ELAPSED" -lt "$CHECK_INTERVAL_SECONDS" ]; then
    DAYS_LEFT=$(( (CHECK_INTERVAL_SECONDS - ELAPSED) / 86400 ))
    echo "Paper checked recently, skipping (next check in ~$DAYS_LEFT day(s))."
    exit 0
  fi
fi

echo "Checking latest stable Paper build for $VERSION..."

DOWNLOAD_URL=$(curl -s -H "User-Agent: $USER_AGENT" \
  "https://fill.papermc.io/v3/projects/paper/versions/$VERSION/builds" \
  | jq -r 'first(.[] | select(.channel == "STABLE") | .downloads."server:default".url) // "null"')

if [ "$DOWNLOAD_URL" == "null" ] || [ -z "$DOWNLOAD_URL" ]; then
  echo "No stable build found for $VERSION — check the version string or API response."
  exit 1
fi

echo "Downloading: $DOWNLOAD_URL"
curl -f -H "User-Agent: $USER_AGENT" -o "$RUN_DIR/paper-latest.jar.tmp" "$DOWNLOAD_URL"

if [ $? -ne 0 ]; then
  echo "Download failed — keeping existing jar."
  rm -f "$RUN_DIR/paper-latest.jar.tmp"
  exit 1
fi

mv "$RUN_DIR/paper-latest.jar.tmp" "$RUN_DIR/paper-latest.jar"
date +%s > "$TIMESTAMP_FILE"
echo "Paper updated successfully."