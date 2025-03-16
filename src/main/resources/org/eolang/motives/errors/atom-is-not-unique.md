# Atom is not unique

All atom FQNs across all `.eo` files must not be duplicated.

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

To fix this, rename duplicated object.
