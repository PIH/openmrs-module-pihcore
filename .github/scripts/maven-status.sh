#!/bin/bash

# Variables from input arguments
GHA_BASE_URL="https://api.github.com/repos"
ORIGIN_URL=$(git remote get-url origin)
OWNER=$(echo ${ORIGIN_URL} | cut -d/ -f4- | cut -d/ -f1)
REPO=$(basename -s .git "${ORIGIN_URL}")
SHA=$(git rev-parse HEAD)
WORKFLOW_FILENAME="workflow.json"

GHA_WORKFLOW_RUNS_URL="${GHA_BASE_URL}/${OWNER}/${REPO}/actions/runs?head_sha=${SHA}"
curl -Ls "${GHA_WORKFLOW_RUNS_URL}" > ${WORKFLOW_FILENAME}

cat "${WORKFLOW_FILENAME}"

HEAD_BRANCH=$(cat "${WORKFLOW_FILENAME}" | jq '.workflow_runs[0].head_branch')
HEAD_SHA=$(cat "${WORKFLOW_FILENAME}" | jq '.workflow_runs[0].head_sha')
CREATED_AT=$(cat "${WORKFLOW_FILENAME}" | jq '.workflow_runs[0].created_at')
STARTED_AT_DATE=$(cat "${WORKFLOW_FILENAME}" | jq '.workflow_runs[0].run_started_at')
UPDATED_AT=$(cat "${WORKFLOW_FILENAME}" | jq '.workflow_runs[0].updated_at')
STATUS=$(cat "${WORKFLOW_FILENAME}" | jq '.workflow_runs[0].status')
CONCLUSION=$(cat "${WORKFLOW_FILENAME}" | jq '.workflow_runs[0].conclusion')

echo "${GHA_WORKFLOW_RUNS_URL}"
echo "--------------------------"
echo "HEAD_BRANCH: ${HEAD_BRANCH}"
echo "HEAD_SHA: ${HEAD_SHA}"
echo "CREATED_AT: ${CREATED_AT}"
echo "STARTED_AT_DATE: ${STARTED_AT_DATE}"
echo "UPDATED_AT: ${UPDATED_AT}"
echo "STATUS: ${STATUS}"
echo "CONCLUSION: ${CONCLUSION}"