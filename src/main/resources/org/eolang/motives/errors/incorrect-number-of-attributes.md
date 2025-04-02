# Incorrect Number Of Attributes

The number of provided attributes to the object should match with the number of
its declared attributes.

Incorrect:

`foo.eo`:

```eo
# Its foo with one attribute.
[at] > foo
```

`bar.eo`:

```eo
# Its bar.
[args] > bar
  foo 1 2
```

Correct:

`foo.eo`:

```eo
# Its foo with one attribute.
[at] > foo
```

`bar.eo`:

```eo
# Its bar.
[args] > bar
  foo 1
```
