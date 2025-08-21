# Redundant object

If a named object is used only once, it is considered "redundant" and should be
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
