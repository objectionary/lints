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

Comments that begin with a tracker marker such as `@todo` or `@fixme`
(case-insensitive) are exempt from this rule:

```eo
# @todo #123:30min Add validation for negative input.
[] > foo
  42 > @
```
