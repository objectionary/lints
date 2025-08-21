# Empty `+spdx` tail

The special meta attribute `+spdx` cannot have an empty value.

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
