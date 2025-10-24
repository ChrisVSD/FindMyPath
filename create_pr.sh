#!/bin/bash
# Helper script to create branch, push, and open PR using GitHub CLI (gh).
# Usage: ./create_pr.sh "Your PR title" "PR body"
set -e
TITLE="$1"
BODY="$2"
BRANCH="feat/add-workmanager-upload-$(date +%s)"
git checkout -b $BRANCH
git add .
git commit -m "$TITLE" || true
git push -u origin $BRANCH
# Requires GitHub CLI (gh) to be installed and authenticated
gh pr create --title "$TITLE" --body "$BODY" --base main --head $BRANCH
echo "PR created for branch $BRANCH"
