# `+package` contains multiple parts

The special `+package` meta must contain exactly one value.

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
