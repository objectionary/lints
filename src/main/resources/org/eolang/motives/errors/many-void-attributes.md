# Many void attributes

Objects must have at most **5** void attributes.

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
