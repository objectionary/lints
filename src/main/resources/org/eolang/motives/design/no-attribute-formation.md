# No attribute formation

Formations without void attributes are not recommended. Such formations
are similar to [Utility classes] in Java.

Incorrect:

```eo
# Foo.
[] > foo
  52 > spb
```

Correct:

```eo
# Foo.
[x] > foo
  x > sbp
```

An "attribute-free" formation is also correct, but only if its parent
is a formation as well:

```eo
# Foo.
[x] > foo
  # Bar has access to `x`.
  [] > bar
```

[Utility classes]: https://www.yegor256.com/2015/02/26/composable-decorators.html
