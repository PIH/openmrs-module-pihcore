#!/bin/bash
# This script polls for the status of github action workflows for the `maven.yml` workflow
# and will return either a success or failure based on the success or failure of the workflow associated with the
# latest commit in the checked-out code

# Variables from input arguments
GHA_BASE_URL="https://api.github.com/repos"
ORIGIN_URL=$(git remote get-url origin)
OWNER=$(echo ${ORIGIN_URL} | cut -d/ -f4- | cut -d/ -f1)
REPO=$(basename -s .git "${ORIGIN_URL}")
SHA=$(git rev-parse HEAD)
FREQUENCY=10  # Check status every X seconds, defaults to 10 seconds
TIMEOUT=1800  # Return with timeout result if no conclusion in X seconds, defaults to 1800 (30 minutes)

ARGUMENTS_OPTS="s:f:t:"
while getopts "$ARGUMENTS_OPTS" opt; do
     case $opt in
        s  ) SHA=$OPTARG;;
        f  ) FREQUENCY=$OPTARG;;
        t  ) TIMEOUT=$OPTARG;;
        \? ) echoerr "Unknown option: -$OPTARG"; help; exit 1;;
        :  ) echoerr "Missing option argument for -$OPTARG"; help; exit 1;;
        *  ) echoerr "Unimplemented option: -$OPTARG"; help; exit 1;;
     esac
done

check_status() {
  GHA_WORKFLOW_RUNS_URL="${GHA_BASE_URL}/${OWNER}/${REPO}/actions/runs?head_sha=${SHA}"
  WORKFLOW_RUNS_RESPONSE=$(curl -Ls "${GHA_WORKFLOW_RUNS_URL}"  2>/dev/null)
  WORKFLOW_RUNS=$(echo ${WORKFLOW_RUNS_RESPONSE} | jq '.workflow_runs')
  MAVEN_RUN=$(echo ${WORKFLOW_RUNS} | jq '.[] | select (.path=".github/workflows/deploy.yml")')
  STATUS_OUTPUT=$(echo "${MAVEN_RUN}" | jq "{
    head_branch: .head_branch,
    head_sha: .head_sha,
    created_at: .created_at,
    updated_at: .updated_at,
    status: .status,
    conclusion: .conclusion
  }")
  echo "${STATUS_OUTPUT}"
}

BUILD_STATUS=""
while [ -z "${BUILD_STATUS}" ] && [ ${TIMEOUT} -gt 0 ]
do
  CURRENT_DATE=$(date '+%Y-%m-%d-%H-%M-%S')
  CURRENT_STATUS=$(check_status)
  echo "${CURRENT_DATE}"
  echo "${CURRENT_STATUS}"
  BUILD_STATUS=$(echo "${CURRENT_STATUS}" | jq -r '.conclusion')
  if [ "${BUILD_STATUS}" == "null" ]; then
    BUILD_STATUS=""
  fi
  if [ -z "${BUILD_STATUS}" ]; then
    echo "Remaining timeout: ${TIMEOUT}"
    sleep ${FREQUENCY}
    TIMEOUT=$((TIMEOUT - FREQUENCY))
  fi
done

if [ "$BUILD_STATUS" == "success" ]; then
  echo "Build Successful"
  exit 0;
elif [ -z "$BUILD_STATUS" ]; then
  echo "Build timed out"
  exit 1;
else
  echo "Build Failed with status: ${BUILD_STATUS}"
  exit 1;
fi
