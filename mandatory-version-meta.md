# Mandatory `+version` Meta

Program should have only one `+version` special meta.

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
+package 0.0.1

[] > foo
```
