# Too deep object

Objects nested deeper than twelve levels are hard to read and to debug.
The nesting level of an object is the number of its ancestor objects.
If it exceeds twelve, a warning is raised.

Incorrect:

```eo
# Foo.
[] > foo
  a > x
    b
      c
        d
          e
            f
              g
                h
                  i
                    j
                      k
                        l
                          m
```

Here, `m` is nested thirteen levels deep, which is hard to follow.
It should be refactored by extracting some of the nested objects into
their own top-level objects.
