# `+package` contains multiple parts

Special `+package` meta must be constructed from exact one part.

Incorrect:

```eo
+package foo bar

[] > foo
```

Correct:

```eo
+package foo.bar

[] > foo
```
