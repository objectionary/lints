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

There are two exceptions for idiomatic prefixes used in EO standard library:
the `as-` prefix for type conversions (such as `as-bytes`, `as-i64`,
and `as-number`) and the `is-` prefix for predicates
(such as `is-empty`, `is-nan`, and `is-finite`).
