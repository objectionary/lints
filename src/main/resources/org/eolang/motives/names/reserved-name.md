# Reserved Name

Each object name should not duplicate already reserved names in `org.eolang.*`
objects [located in home][home].

Incorrect:

```eo
# Foo.
[] > foo
  52 > true
```

Since `true` already exists in `org.eolang.true.eo`.

[home]: https://github.com/objectionary/home/tree/master/objects/org/eolang
