# Mystery object

Objects, applied in the code, must be defined somewhere in the program,
be the objects of `org.eolang.*` or be imported via `+alias` meta. Free
usage of an unknown object is a mystery object — it is almost always a
typo in the name, since this object is not declared anywhere.

Incorrect:

```eo
# Foo.
[] > foo
  bar 42 > x
```

Here, `bar` is not defined in the program, not an object from `org.eolang.*`
and not imported via `+alias`. It will be compiled to `Φ.bar` reference,
which can't be validated by the parser. This is what we call a mystery
object, and it should be fixed by adding the definition of `bar`, importing
it, or using a proper object name.
