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

There are exceptions for idiomatic prefixes and suffixes.
The `as-` prefix is for type conversions: `as-bytes`, `as-i64`, `as-number`.
The `is-` prefix is for predicates: `is-empty`, `is-nan`, `is-finite`.
The `-of` suffix is for extracting parts: `slice-of`, `value-of`, `length-of`.
