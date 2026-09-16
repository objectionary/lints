# Sparse `Φ.seq`

The `Φ.seq` object should have more than one object inside.

Incorrect:

```eo
# App.

[] > app
  seq > @
    *
      true
```

Correct:

```eo
# App.

[] > app
  seq > @
    *
      42
      true
```
