# Inconsistent Args

Objects with the same `@base` should have the same amount of arguments passed
in it.

Incorrect:

```eo
# Foo.
[] > foo
  bar 42 > x
  bar 1 2 3 > y
```

Correct:

```eo
# Foo.
[] > foo
  bar 42 > x
  bar 1 > y
```
