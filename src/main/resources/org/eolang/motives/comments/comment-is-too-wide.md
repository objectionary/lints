# Comment Is Too Wide

Each comment should not be wider than 80 characters.

Incorrect:

```eo
# This is a very long comment that contains more than eighty characters and should be flagged by the lint as too wide.
[] > foo
```

Correct:

```eo
# This is a good comment.
[] > foo
```
