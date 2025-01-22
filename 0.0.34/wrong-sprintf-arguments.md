# Wrong `QQ.txt.sprintf` Arguments

The `QQ.txt.sprintf` object must have a match between its placeholder variables
and passed arguments.

Incorrect:

```eo
[] > app
  QQ.io.stdout > @
    QQ.txt.sprintf
      "Hello, %s! Your account is %d."
      * name
```

Correct:

```eo
[] > app
  QQ.io.stdout > @
    QQ.txt.sprintf
      "Hello, %s! Your account is %d."
      * name acc
```
