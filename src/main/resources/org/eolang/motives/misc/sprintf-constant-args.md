# `.printf` With Constant String Arguments Only

Using `.printf` makes no sense when the format template and all
the arguments filling its placeholders are constant strings. The result
is already known, so a plain literal string can be used instead.

Incorrect:

```eo
[] > app
  io.stdout > @
    "%s %s".printf
      * "hello" "world!"
```

Correct:

```eo
[] > app
  io.stdout > @
    "hello world!"
```
