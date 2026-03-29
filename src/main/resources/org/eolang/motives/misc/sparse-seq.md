# Sparse `Φ.seq`

The `Φ.seq` object should have more than one object inside.

Incorrect:

```eo
# App.
[] > app
  QQ.seq > @
    *
      true
```

Correct:

```eo
# App.
[] > app
  QQ.seq > @
    *
      42
      true
```
