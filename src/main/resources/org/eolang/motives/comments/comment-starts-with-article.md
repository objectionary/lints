# Comment Starts With An Article

Object comments should not start with an article ("A", "An", or "The"). Such
articles add no information and make comments unnecessarily verbose. Begin the
comment directly with the meaningful word instead.

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
