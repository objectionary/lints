# `+package` Without Tail

Special `+package` meta must have a tail.

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
