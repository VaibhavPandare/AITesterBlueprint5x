#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
SKILL_DIR="$(cd -- "$SCRIPT_DIR/.." && pwd)"
ENV_FILE="$SKILL_DIR/.env"

if [[ $# -ne 1 ]]; then
  printf 'Usage: %s <JIRA-KEY>\n' "$0" >&2
  exit 2
fi

ISSUE_KEY="$1"
if [[ ! "$ISSUE_KEY" =~ ^[A-Za-z][A-Za-z0-9_]*-[0-9]+$ ]]; then
  printf 'Invalid Jira issue key: %s\n' "$ISSUE_KEY" >&2
  exit 2
fi

if [[ -f "$ENV_FILE" ]]; then
  while IFS= read -r line || [[ -n "$line" ]]; do
    line="${line%$'\r'}"
    [[ "$line" =~ ^[[:space:]]*$ || "$line" =~ ^[[:space:]]*# ]] && continue
    [[ "$line" == *=* ]] || continue

    key="${line%%=*}"
    value="${line#*=}"
    key="${key//[[:space:]]/}"
    value="${value#"${value%%[![:space:]]*}"}"
    value="${value%"${value##*[![:space:]]}"}"
    if [[ ${#value} -ge 2 ]]; then
      if [[ "${value:0:1}" == '"' && "${value: -1}" == '"' ]] ||
         [[ "${value:0:1}" == "'" && "${value: -1}" == "'" ]]; then
        value="${value:1:${#value}-2}"
      fi
    fi

    case "$key" in
      JIRA_BASE_URL)
        [[ -n "${JIRA_BASE_URL:-}" ]] || export JIRA_BASE_URL="$value"
        ;;
      JIRA_EMAIL)
        [[ -n "${JIRA_EMAIL:-}" ]] || export JIRA_EMAIL="$value"
        ;;
      JIRA_TOKEN)
        [[ -n "${JIRA_TOKEN:-}" ]] || export JIRA_TOKEN="$value"
        ;;
    esac
  done < "$ENV_FILE"
fi

if [[ -z "${JIRA_BASE_URL:-}" || -z "${JIRA_EMAIL:-}" || -z "${JIRA_TOKEN:-}" ]]; then
  printf 'Set JIRA_BASE_URL, JIRA_EMAIL, and JIRA_TOKEN in %s or the environment.\n' "$ENV_FILE" >&2
  exit 2
fi

if [[ ! "$JIRA_BASE_URL" =~ ^https://[^/]+(/.*)?$ ]]; then
  printf 'JIRA_BASE_URL must be an HTTPS Jira site URL.\n' >&2
  exit 2
fi

JIRA_BASE_URL="${JIRA_BASE_URL%/}"

curl --fail --silent --show-error \
  --request GET \
  --url "$JIRA_BASE_URL/rest/api/3/issue/$ISSUE_KEY" \
  --user "$JIRA_EMAIL:$JIRA_TOKEN" \
  --header 'Accept: application/json'
