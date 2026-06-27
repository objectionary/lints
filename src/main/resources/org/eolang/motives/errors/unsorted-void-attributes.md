# Unsorted void attributes

Void attributes (free parameters) of a formation must be listed in
alphabetical order. A consistent order makes the signature of an object
easier to read and avoids arbitrary, meaningless differences between
formations.

Incorrect:

```eo
# No comments.
[b a c] > foo
  something > @
```

Correct:

```eo
# No comments.
[a b c] > foo
  something > @
```

Only the void attributes of a single formation are compared with each
other. The bound attributes in the body are not affected.
