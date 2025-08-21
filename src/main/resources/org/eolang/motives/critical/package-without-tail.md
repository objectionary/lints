# `+package` without tail

The special `+package` meta must have a value.

Incorrect:

```eo
+package

# Foo.
[] > foo
```

Correct:

```eo
+package foo

# Foo.
[] > foo
```
