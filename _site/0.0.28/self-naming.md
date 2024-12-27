# Self Naming

Object must not refer to itself.

Incorrect:

```eo
[] > foo
  a > a
```

Correct:

```eo
[] > foo
  a > b
```
