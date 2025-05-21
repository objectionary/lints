# Many Void Attributes

Object must have at **max 5** void  attributes.

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
