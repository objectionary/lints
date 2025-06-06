# Duplicate names

The names of objects in the same file (even if the names are in different
scopes) should be different so that there is no confusion

Incorrect:

```eo
[] > foo
  [] > a1
    42 > bar
  [] > a2
    33 > bar
```

Correct:

```eo
[] > foo
  [] > a1
    42 > bar
  [] > a2
    33 > klub
```
