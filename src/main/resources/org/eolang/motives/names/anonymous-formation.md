# Anonymous formation

Anonymous formations should not access objects that are undefined within the formation.

Incorrect:

```eo
# App.

[] > app
  "Hello" > t
  malloc.of > @
    64
    [m]
      io.stdout > @
        ^.t
```

Correct:

```eo
# App.

[] > app
  "Hello" > t
  malloc.of > @
    64
    [m]
      "boom" > x
      io.stdout > @
        x
```
