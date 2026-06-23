# Syntax version mismatch

The `+syntax` meta specifies the version of the EO language that
the source code was written for. If the parser version is older
than the version specified in `+syntax`, the code may use features
that are not supported by the parser, leading to compilation errors.

The parser will refuse to process such files.

Incorrect (if parser version is 0.58.0):

```eo
+syntax 0.59.0

[] > foo
```

Correct (if parser version is 0.59.0 or newer):

```eo
+syntax 0.59.0

[] > foo
```
