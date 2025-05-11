# Attribute name should not be compound

In the code, all objects that are not formations
should have a single word in their name.
This is because if the name is compound,
it means the context in the program is too large,
and it needs to be decomposed.

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
