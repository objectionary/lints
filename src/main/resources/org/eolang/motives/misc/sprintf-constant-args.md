# `QQ.txt.sprintf` With Constant String Arguments Only

Using `QQ.txt.sprintf` makes no sense when the format template and all
the arguments filling its placeholders are constant strings. The result
is already known, so a plain literal string can be used instead.

Incorrect:

```eo
[] > app
  QQ.io.stdout > @
    QQ.txt.sprintf
      "%s %s"
      * "hello" "world!"
```

Correct:

```eo
[] > app
  QQ.io.stdout > @
    "hello world!"
```
