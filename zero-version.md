# Zero Version

Objects inside `src/(main|test)` must use `0.0.0` version during the
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
