# Incorrect `+package`

The special meta `+package` must follow this regular expression:

```regexp
^[a-z][a-z0-9_]*(\.[a-z0-9_]+)+[0-9a-z_]$
```

Incorrect:

```eo
+package foo
+package foo.x
+package привет, как дела?
+package привет.как

[] > foo
```

Correct:

```eo
+package foo.bar
+package my.awesome.package
+package test.xy

[] > foo
```
