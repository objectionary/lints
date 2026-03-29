# Attribute Names Should Not Be Compound

All objects that are not formations must have single-word names.
Compound names indicate that the program context is too large
and needs to be decomposed.

Incorrect:

```eo
# CSV.
[csv-file-name] > csv-object
  parse > read-records
    read csv-file-name
```

Correct:

```eo
# CSV.
[file] > csv-object
  parse > records
    read file
```

There are exceptions for idiomatic prefixes and suffixes used in EO standard library:
the `as-` prefix for type conversions (such as `as-bytes`, `as-i64`,
and `as-number`), the `is-` prefix for predicates
(such as `is-empty`, `is-nan`, and `is-finite`), and the `-of` suffix
for extracting parts (such as `slice-of`, `value-of`, and `length-of`).
