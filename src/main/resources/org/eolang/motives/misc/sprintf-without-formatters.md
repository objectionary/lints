# `Φ.org.eolang.txt.sprintf` without formatters

Using the `Φ.org.eolang.txt.sprintf` object makes no sense if there are no
format specifiers in the template string.

Incorrect:

```eo
[] > app
QQ.io.stdout > @
  QQ.txt.sprintf
    "Hello Jeff!"
    *
```

Correct:

```eo
[] > app
QQ.io.stdout > @
  QQ.txt.sprintf
    "Hello %s!"
    * "Jeff"
```
