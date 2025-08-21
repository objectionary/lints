# Incorrect `+rt` parts

The special `+rt` meta must have exactly two parts: the runtime name
and the runtime location.

Incorrect:

```eo
+rt jvm
+rt jvm eolang

[] > foo
```

Correct:

```eo
+rt jvm org.eolang:eo-runtime:0.0.0

[] > foo
```
