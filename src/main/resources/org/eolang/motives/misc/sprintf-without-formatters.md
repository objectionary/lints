# `.printf` without formatters

Using the `.printf` object makes no sense if there are no
format specifiers in the template string.

Incorrect:

```eo
[] > app
  io.stdout > @
    "Hello Jeff!".printf
      *
```

Correct:

```eo
[] > app
  io.stdout > @
    "Hello %s!".printf
      * "Jeff"
```
