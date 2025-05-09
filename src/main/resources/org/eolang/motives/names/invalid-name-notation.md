# Invalid name notation

The object name must match the regular expression `[a-z]+(-[a-z]+)*`.
Basically, it should follow kebab-case style,
but using only Latin letters - no digits or other characters.

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
