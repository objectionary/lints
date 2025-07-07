# Redundant Object

If named object used only once, it's treated as "redundant", and can be
inlined.

Incorrect:

```eo
# Foo.
[] > foo
  52 > spb
  spb.plus 2
```

Correct:

```eo
# Foo.
[] > foo
  52.plus 2
```
