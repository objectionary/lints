# Invalid name notation

The name of any object must match the regular expression `[a-z]+(-[a-z]+)*`.
Basically, it must follow kebab-case notation, using Latin letters alone.
No digits, no underscores, no uppercase characters.

Incorrect:

```eo
# App.
[] > mainApp
  foo > x1
  bar > y_
```

Correct:

```eo
# App.
[] > main-app
  foo > x
  bar > y
```
