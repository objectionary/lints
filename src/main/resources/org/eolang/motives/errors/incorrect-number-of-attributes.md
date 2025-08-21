# Incorrect number of attributes

The number of provided attributes must match the number of
declared attributes for an object.

Incorrect:

`foo.eo`:

```eo
# Foo with one attribute.
[at] > foo
```

`bar.eo`:

```eo
# Bar object.
[args] > bar
  foo 1 2
```

Correct:

`foo.eo`:

```eo
# Foo with one attribute.
[at] > foo
```

`bar.eo`:

```eo
# Bar object.
[args] > bar
  foo 1
```
