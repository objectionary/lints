# Unknown Names

Used object must be defined in the program.

Incorrect:

```eo
[] > foo
  test > @
```

Correct:

```eo
+alias test

[] > foo
  test > @
```
