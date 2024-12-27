# Incorrect `+package`

Special meta `+package` should follow regexp:

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
