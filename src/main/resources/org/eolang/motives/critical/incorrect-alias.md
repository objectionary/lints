# Incorrect alias

The special meta `+alias` must point to an existing file in the directory specified by
the `+package` meta.

Incorrect:

```eo
+alias foo
+package ttt

# Bar.
[] > bar
  foo > @
```

Since the `ttt/foo.xmir` file doesn't exist, a `critical` defect will be issued.
