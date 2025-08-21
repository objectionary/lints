# Correct `+package` Meta

The special meta `+package` must have a valid value.

Incorrect:

```eo
+package

[] > foo
```

Correct:

```eo
+package my.awesome.package

[] > foo
```
