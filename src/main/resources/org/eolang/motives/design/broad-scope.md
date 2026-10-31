# Broad scope

A private attribute should be declared as close as possible to its one
usage. If a private attribute is used inside a single nested object alone,
declaring it on the level of the parent object makes its scope unnecessarily
broad.

Incorrect:

```eo
[] > foo
  42 >> a
  [] >> b
    a.plus 1 > c
```

Here, the scope of `a` is too broad: `a` gets used inside `b` alone. It should be
moved closer, into `b`:

```eo
[] > foo
  [] >> b
    42 >> a
    a.plus 1 > c
```
