# Broken Refs

Object ref must follow regex:

```regexp
^[0-9]+(\.[0-9]+)*$
```

Object ref must not refer to the absent object.

@todo #19:30min Document more about `broken-refs` lint.
 Currently, there is no unit test that finds defects with broken refs.
 Let's add a few, and only then add more docs in broken-ref's motive.
TBD..
