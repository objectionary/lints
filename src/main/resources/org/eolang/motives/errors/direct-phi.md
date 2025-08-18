# Direct phi

The direct usage of `@` attribute is prohibited.

Incorrect:

```eo
[] > foo
  x.@.y > z
```

Correct:

```eo
[] > foo
  x.y > z
```
