#!/usr/bin/env bash

# SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
# SPDX-License-Identifier: MIT

set -e -o pipefail

baseline=$1
current=$2
pr=$3

if [ -z "${baseline}" ] || [ -z "${current}" ] || [ -z "${pr}" ]; then
  echo "Usage: benchmark-diff.sh <baseline.csv> <current.csv> <pr-number>"
  exit 1
fi

diff=$(mktemp)
trap 'rm -f "${diff}"' EXIT

awk -F, '
  function unquote(s) { gsub(/"/, "", s); return s }
  FNR == 1 { next }
  NR == FNR { base[unquote($1)] = unquote($2); next }
  {
    id = unquote($1)
    curr[id] = unquote($2)
    seen[id] = 1
  }
  END {
    for (id in base) { seen[id] = 1 }
    for (id in seen) {
      if ((id in base) && (id in curr)) {
        delta = curr[id] - base[id]
        printf "%s\t%s\t%s\t%d\n", id, base[id], curr[id], delta
      } else if (id in curr) {
        printf "%s\t%s\t%s\t%s\n", id, "-", curr[id], "new"
      } else {
        printf "%s\t%s\t%s\t%s\n", id, base[id], "-", "removed"
      }
    }
  }
' "${baseline}" "${current}" | sort -t $'\t' -k1,1 > "${diff}"

total=$(wc -l < "${diff}" | tr -d ' ')

as_table() {
  while IFS=$'\t' read -r id before after delta; do
    if [ "${delta}" = "new" ]; then
      printf '| %s | - | %s ms | new |\n' "${id}" "${after}"
    elif [ "${delta}" = "removed" ]; then
      printf '| %s | %s ms | - | removed |\n' "${id}" "${before}"
    else
      printf '| %s | %s ms | %s ms | %+d ms |\n' "${id}" "${before}" "${after}" "${delta}"
    fi
  done
}

biggest_movers() {
  # head truncates the pipeline early, which SIGPIPEs the upstream writer;
  # pipefail would otherwise treat that expected broken pipe as a failure.
  (
    set +o pipefail
    grep -v -e $'\tnew$' -e $'\tremoved$' "${diff}" \
      | awk -F'\t' '{ d = $4; if (d < 0) { d = -d }; printf "%d\t%s\n", d, $0 }' \
      | sort -t $'\t' -k1,1 -n -r \
      | cut -f2- \
      | head -10
  )
}

printf '## Benchmark comparison for #%s\n\n' "${pr}"
# shellcheck disable=SC2016
printf 'Comparing this branch against the `master` baseline in `benchmark/lint-timings.csv`. Positive deltas are slower, negative are faster.\n\n'
printf '### Biggest changes\n\n'
printf '| lint (size) | before | after | delta |\n'
printf '|---|---|---|---|\n'
biggest_movers | as_table
printf '\n<details>\n<summary>Full results (%s entries)</summary>\n\n' "${total}"
printf '| lint (size) | before | after | delta |\n'
printf '|---|---|---|---|\n'
as_table < "${diff}"
printf '\n</details>\n'
