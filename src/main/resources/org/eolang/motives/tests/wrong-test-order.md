# Wrong test order

Unit tests must be located after live objects:

Incorrect:

```eo
[] > foo
  [] +> runs-something
  [] > bar
```

Correct:

```eo
[] > foo
  [] > bar
  [] +> runs-something
```
