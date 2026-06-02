# Sprintf with constant arguments

When all arguments to `sprintf` are constants, the formatted string is
itself a constant and `sprintf` is unnecessary overhead.

Incorrect:

```eo
sprintf "%s world" "hello"
```

Correct:

```eo
"hello world"
```
