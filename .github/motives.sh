#!/bin/bash

# SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
# SPDX-License-Identifier: MIT

set -e -o pipefail

tag=$1
if [ -z "${tag}" ]; then
  echo "Provide tag name as the first argument, for example '0.0.1'"
  exit 1
fi

rm -rf "gh-pages/${tag}"
mkdir -p "gh-pages/${tag}"

list_them() {
  printf "<ul>\n"
  while IFS= read -r f; do
    n=$(basename "${f}" ".html")
    printf '<li><a href="%s">%s</a></li>\n' \
      "/lints/${tag}/${n}.html" "${n}"
  done < <(find "gh-pages/${tag}" -name '*.html' -not -name 'index.html' | sort)
  printf '</ul>\n'
}

head() {
  printf '<html>\n'
  printf '<head>\n'
  printf '<meta charset="UTF-8"/>\n'
  printf '<title>lints</title>\n'
  printf '<meta name="viewport" content="width=device-width, initial-scale=1.0"/>\n'
  printf '<link rel="shortcut icon" href="https://www.yegor256.com/images/books/elegant-objects/cactus.png"/>'
  printf '<link href="https://cdn.jsdelivr.net/npm/tacit-css@1.8.1/dist/tacit-css.min.css" rel="stylesheet" integrity="sha384-JbsYayq5Otme+gjh/pl7NrA/qMIU0bxbdzKvYqQGHvvag0lHhM62TQnDzz+EyzXj" crossorigin="anonymous"/>\n'
  printf '</head>\n'
  printf '<body><section><article>\n'
}

tail() {
  printf '<p>Published on %s.</p>\n' "$(date)"
  printf '</article></section></body></html>'
}

while IFS= read -r f; do
  n=$(basename "${f}" .md)
  html=gh-pages/${tag}/${n}.html
  ( head && pandoc "${f}" && tail ) > "${html}"
  echo "${n} -> $(du -b "${html}" | cut -f1) bytes"
done < <(find src/main/resources/org/eolang/motives -name '*.md' | sort)

(
  head
  printf '<p>Latest version: <a href="/lints/%s/index.html">%s</a></p>\n' "${tag}" "${tag}"
  printf '<p>List of lints in this version:<p>'
  list_them
  tail
) > "gh-pages/index.html"

(
  head
  printf "<p>List of lints in %s:<p>" "${tag}"
  list_them
  tail
) > "gh-pages/${tag}/index.html"
