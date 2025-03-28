# Reserved Name

Each named object should not duplicate already reserved names in `org.eolang.*`
objects from [home].

Incorrect:

```eo
# Foo.
[] > foo
  52 > true
```

Since `true` already exists in `org.eolang.true.eo`.

[home]: https://github.com/objectionary/home
