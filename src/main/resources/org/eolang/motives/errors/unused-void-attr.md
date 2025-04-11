# Unused Void Attr

Declared void attribute should be used inside the formation.

Incorrect:

```eo
# Foo.
[x] > foo
  42 > bar
```

Correct:

```eo
# Foo.
[x] > foo
  x > bar
```

Unused void attributes are allowed only if there are atoms in the program:

```eo
# Foo.
[x] > foo
  [] > bar ?
```

Or formation itself is the atom:

```eo
# Foo.
[x] > foo ?
```
