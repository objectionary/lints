# Comment Starts With Article

Comments should not start with an article, such as `a`, `an`, or `the`.
An article at the beginning of a comment adds no information and makes
the documentation more verbose than necessary.

Incorrect:

```eo
# The object that calculates the sum.
[] > foo
  42 > @
```

Correct:

```eo
# Object that calculates the sum.
[] > foo
  42 > @
```

The check is case-insensitive, so `A`, `An`, and `The` are flagged as
well. Words that merely begin with the same letters as an article, such
as `Another` or `Theory`, are not affected.
