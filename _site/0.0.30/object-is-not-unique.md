# Object Is Not Unique

High-level object names must be unique across the package.

Incorrect:

`foo.eo`:

```eo
+package foo

# Foo.
[] > foo
```

`bar.eo`:

```eo
+package foo

# Bar.
[] > foo
```

Correct:

`foo.eo`:

```eo
+package foo

# Foo.
[] > foo
```

`bar.eo`:

```eo
+package foo

# Bar.
[] > bar
```
