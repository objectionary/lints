# `Q.org.eolang.txt.sprintf` Without Formatters

The use of `Q.org.eolang.txt.sprintf` object makes no sense if there is no
formatters in template string.

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
