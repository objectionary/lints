# Zero version

Objects inside `src/(main|test)` must use version `0.0.0` during
development.

Incorrect:

```eo
+version 1.2.3

[] > foo
```

Correct:

```eo
+version 0.0.0

[] > foo
```
