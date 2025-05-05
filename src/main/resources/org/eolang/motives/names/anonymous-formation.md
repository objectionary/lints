# Anonymous formation

Anonymous formation shouldn't access object, undefined inside the formation.

Incorrect:

```eo
# App.
[] > app
  "Hello" > t
  malloc.of
    64
    [m]
      QQ.io.stdout > @
        t
```

Correct:

```eo
# App.
[] > app
  "Hello" > t
  malloc.of
    64
    [m]
      "boom" > x
      QQ.io.stdout > @
        x
```
