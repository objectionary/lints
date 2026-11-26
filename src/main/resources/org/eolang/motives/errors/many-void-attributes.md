# Excessive void attributes

Objects must have at most **5** void attributes.
The receiver `^` is not counted, even when declared explicitly.

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

Also correct, since `^` is the receiver, not an argument:

```eo
[^ a b c d e] > with-receiver
  something > @
```
