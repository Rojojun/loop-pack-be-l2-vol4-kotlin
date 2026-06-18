#!/usr/bin/env bash
# OTEL Java Agent 다운로드 (auto-instrumentation: HTTP/JDBC/Lettuce span 자동 생성).
# jar 은 바이너리라 git 에 커밋하지 않는다(.gitignore 처리). 측정 전 1회 실행.
set -euo pipefail

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR="$DIR/opentelemetry-javaagent.jar"
URL="https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar"

if [[ -f "$JAR" ]]; then
  echo "이미 존재: $JAR (다시 받으려면 삭제 후 재실행)"
  exit 0
fi

echo "다운로드 중: $URL"
curl -fsSL -o "$JAR" "$URL"
echo "완료: $JAR"
