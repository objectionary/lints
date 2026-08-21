# Comment Is Too Wide

Each comment should not be wider than 100 characters.

Incorrect:

```eo
# This is a very long comment that contains more than 100 characters and should be flagged by the lint as too wide.
[] > foo
```

Correct:

```eo
# This is a good comment.
[] > foo
```
