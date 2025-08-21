# Self referencing

Objects must not reference themselves.

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
