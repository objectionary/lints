# Invalid name notation

The name of any object must match the regular expression `[a-z][a-z0-9]*(-[a-z0-9]+)*`.
Basically, it must follow kebab-case notation and use Latin letters and digits alone.
It must start with a letter, no underscores, no uppercase characters.

Incorrect:

```eo
# App.
[] > mainApp
  foo > x_
  bar > y_
```

Correct:

```eo
# App.
[] > main-app
  foo > x
  bar > y
```
