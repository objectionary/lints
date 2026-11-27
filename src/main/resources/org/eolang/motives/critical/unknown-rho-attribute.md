# Unknown Rho Attribute

A reference through `^` must point to an attribute that exists
in the formation it lands in.
Each `^` goes up by one formation, so the compiler knows statically
which formation the reference lands in, and a missing attribute
there is a typo or a leftover after a rename.

Incorrect:

```eo
[] > foo
  "yes" > message
  [] > bar
    ^.messageeee > @
```

Correct:

```eo
[] > foo
  "yes" > message
  [] > bar
    ^.message > @
```
