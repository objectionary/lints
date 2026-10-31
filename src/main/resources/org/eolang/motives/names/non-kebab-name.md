# Formation Names Must Be in Kebab-case

Formation names must use kebab-case: all lowercase letters and digits,
with words separated by hyphens.
`camelCase` and `snake_case` formation names are not allowed.

Incorrect:

```eo
# CsvObject.

[] > csvObject
```

```eo
# Csv object.

[] > csv_object
```

Correct:

```eo
# Csv object.

[] > csv-object
```
