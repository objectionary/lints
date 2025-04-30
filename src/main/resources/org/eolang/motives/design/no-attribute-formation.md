# No Attribute Formation

It's not recommended to have formation without void attributes. Such formations
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

It is also correct having an "attribute-free" formation, but only if its parent
is a formation as well:

```eo
# Foo.
[x] > foo
  # Bar, have an access to `x`.
  [] > bar
```

[Utility classes]: https://www.yegor256.com/2015/02/26/composable-decorators.html
