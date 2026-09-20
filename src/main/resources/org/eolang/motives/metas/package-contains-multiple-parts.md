# `+package` contains more than one part

The special `+package` meta must contain a single value.

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
