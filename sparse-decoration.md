# Sparse Decoration

Sparse decoration of the base object is prohibited.

Incorrect:

```eo
[] > decorates-five
  five > @
```

Correct:

```eo
[] > decorates-application
  if > @
    true
    5
    five
```

```eo
[free] > decorates-with-free-args
  five > @
```
