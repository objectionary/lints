# Wrong `.printf` Arguments

The `.printf` object must have matching placeholder variables
and passed arguments.

Incorrect:

```eo
[name] > app
  io.stdout > @
    "Hello, %s! Your account is %d.".printf
      * name
```

Correct:

```eo
[name acc] > app
  io.stdout > @
    "Hello, %s! Your account is %d.".printf
      * name acc
```
