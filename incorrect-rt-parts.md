# Incorrect `+rt` parts

Special `+rt` meta must have exactly two parts, which is the name of the
runtime, and the location of the runtime.

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
