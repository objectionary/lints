# Atom isn't unique

Atom fully qualified names (FQNs) must be unique across all `.eo` files.

Incorrect:

`foo.eo`:

```eo
+package xyz

# A.
[] > a /number
```

`bar.eo`:

```eo
+package xyz

# A.
[] > a /number
```

To fix this, rename the duplicated object.
