# Mandatory `+version` Meta

The program must have exactly one `+version` special meta.

Incorrect:

```eo
[] > foo
```

```eo
+version 0.0.1
+version 0.0.2

[] > foo
```

Correct:

```eo
+version 0.0.1

[] > foo
```
