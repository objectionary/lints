# Empty `+spdx` tail

The special meta attribute `+spdx` can't have an empty tail.

Incorrect:

```eo
+spdx

# Foo.
[] > foo
```

Correct:

```eo
+spdx foo

# Foo.
[] > foo
```
