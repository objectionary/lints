# Incorrect Alias

Special meta `+alias` must point to existing file in the dir, outlined by
`+package` meta.

Incorrect:

```eo
+alias foo
+package ttt

# Bar.
[] > bar
  foo > @
```

Since `ttt/foo.xmir` file doesn't exist, `critical` defect will be issued.
