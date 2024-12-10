# Incorrect `+rt` jvm location

The location of JVM runtime should follow the regexp:

```regexp
^([a-zA-Z0-9_.-]+):([a-zA-Z0-9_.-]+):(\d+\.\d+\.\d+)$
```

Incorrect:

```eo
+rt jvm test
+rt jvm foo:bar
+rt jvm привет!

# No comments.
[] > foo
```

Correct:

```eo
+rt jvm org.eolang:eo-runtime:0.0.0
+rt jvm foo:bar:0.0.1
+rt jvm the:great:gatsby:1.2.3

# No comments.
[] > foo
```
