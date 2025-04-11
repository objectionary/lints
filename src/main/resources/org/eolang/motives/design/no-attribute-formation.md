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

[Utility classes]: https://www.yegor256.com/2015/02/26/composable-decorators.html
