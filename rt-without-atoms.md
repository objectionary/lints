# `+rt` Without Atoms

Special `+rt` meta must be used only with atoms.

Incorrect:

```eo
+rt node eo2js-runtime:0.0.0

[attr] > foo
```

Correct:

```eo
+rt node eo2js-runtime:0.0.0

[attr] > foo
  [] > test /int
```
