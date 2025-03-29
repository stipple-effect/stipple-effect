#!/bin/bash

echo "Started build..."

separator() {
  echo "================"
}

separator
bash linux_build.sh
separator
cmd.exe /c win_build.bat
separator
echo "Finished build!"
