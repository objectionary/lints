# Comment Not Capitalized

Comments must start with a capital letter.

Incorrect:

```eo
# this comment doesn't start with a capital letter.
[] > foo
  42 > @
```

Correct:

```eo
# This comment does start with a capital letter.
[] > foo
  42 > @
```

Comments starting with an `@`-marker, such as `@todo` or `@fixme`, are
exempt from this check:

```eo
# @todo implement this feature.
[] > foo
  42 > @
```
