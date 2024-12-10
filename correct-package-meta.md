# Correct `+package` Meta

Special meta `+package` must have a correct tail.

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
