# Long live object

An object should be used close to the place where it is defined. If it
is defined many attributes away from its first usage, the code becomes
hard to read: the reader has to keep the object in mind while scanning
the attributes in between.

Incorrect:

```eo
# App.
[] > app
  foo 42 > f
  x > w1
  y > w2
  z > w3
  q > w4
  s > w5
  t > w6
  52.plus f > r
```

Here, `f` is defined and then used seven attributes later. It is better
to move `f` right before its usage:

```eo
# App.
[] > app
  x > w1
  y > w2
  z > w3
  q > w4
  s > w5
  t > w6
  foo 42 > f
  52.plus f > r
```

The distance is measured in sibling attributes between the definition
and the first usage, not in source lines, so the bodies of the objects
in between do not matter.
