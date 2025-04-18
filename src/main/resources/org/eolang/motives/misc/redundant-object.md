# Redundant Object

If named object used only once, it's treated as "redundant", and can be
inlined.

Incorrect:

```eo
# Foo.
[] > foo
  52 > spb
  spb.plus 2
```

Correct:

```eo
# Foo.
[] > foo
  52.plus 2
```

The `Q.org.eolang.structs.list` objects are not considered as "redundant", if they
reused in the loop, like this:

```eo
# Foo.
[] > foo
  list > created
    * 1 2 3
  list
    * 1 2 3
  .eachi > @
    [item index] >>
      withi.
        created
        index
        item
```
