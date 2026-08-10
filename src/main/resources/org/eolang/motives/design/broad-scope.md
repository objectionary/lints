# Broad scope

A private attribute should be declared as close as possible to its only
usage. If a private attribute is used only inside a single nested object,
declaring it on the level of the parent object makes its scope unnecessarily
broad.

Incorrect:

```eo
[] > foo
  42 >> a
  [] >> b
    a.plus 1 > c
```

Here, the scope of `a` is too broad: it is used only inside `b`. It should be
moved closer, into `b`:

```eo
[] > foo
  [] >> b
    42 >> a
    a.plus 1 > c
```
