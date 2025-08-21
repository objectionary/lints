# Duplicate names

Object names in the same file must be unique, even across different
scopes, to avoid confusion.

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
