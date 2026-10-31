# `+rt` without atoms

The special `+rt` meta must be used with atoms, not with anything else.

Incorrect:

```eo
+rt node eo2js-runtime:0.0.0

[attr] > foo
```

Correct:

```eo
+rt node eo2js-runtime:0.0.0

[attr] > foo
  [] > test ?
```
