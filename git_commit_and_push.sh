#!/bin/bash
#set git add all commit with message and push in one run
if [ $# -eq 0 ]
  then
    echo "Error: No commit message present! Breaking add, commit and push"
	exit 0
fi
git add -A
git commit -m "$1"
git push
