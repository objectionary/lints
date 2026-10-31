# Mandatory `+version` Meta

The program must have exactly one `+version` special meta.

Incorrect:

```eo
[] > foo
```

Correct:

```eo
+version 0.0.1

[] > foo
```
