#/!/bin/bash
current_dir=$(pwd)
ln -s "$current_dir/pre-commit" "$current_dir/.git/hooks/pre-commit"