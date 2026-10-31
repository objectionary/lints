# Empty `+spdx` tail

The special meta attribute `+spdx` cannot have an empty value.

Incorrect:

```eo
# Foo.

+spdx

[] > foo
```

Correct:

```eo
# Foo.

+spdx foo

[] > foo
```
