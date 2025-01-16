# Many Free Attributes

Object must have at **max 5 attributes**.

Incorrect:

```eo
[a b c d e f] > with-many
  something > @
```

Correct:

```eo
[a b c d e] > with-ok
  something > @
```
