# Reserved name

Object names must not duplicate reserved names from `org.eolang.*`
objects [located in home][home].

Incorrect:

```eo
# Foo.
[] > foo
  52 > true
```

Since `true` already exists in `org.eolang.true.eo`.

[home]: https://github.com/objectionary/home/tree/master/objects/org/eolang
