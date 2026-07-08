#!/usr/bin/env bash
set -euo pipefail

# Archive any leftover 'Rental' or 'Vehicle' named Postman folders to postman/deprecated_archive
ARCHIVE_DIR="postman/deprecated_archive"
mkdir -p "$ARCHIVE_DIR"

CANDIDATES=(
  "postman/collections/API Tests - Local/Rental Workflow"
  "postman/collections/API Tests - Local/Rentals"
  "postman/collections/API Tests - Render/Rental Workflow"
  "postman/collections/API Tests - Render/Rentals"
)

for d in "${CANDIDATES[@]}"; do
  if [ -d "$d" ]; then
    dest="$ARCHIVE_DIR/$(basename "$d")"
    echo "Moving $d -> $dest"
    mv -v "$d" "$dest"
  else
    echo "Not found (skipping): $d"
  fi
done

echo "Archived rental-related Postman folders to $ARCHIVE_DIR"

