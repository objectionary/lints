# Duplicate Metas

Metas must not be duplicated.

Incorrect:

```eo
+home https://github.com/objectionary/eo
+home https://github.com/objectionary/eo

[] > foo
```

Correct:

```eo
+home https://github.com/objectionary/eo

[] > foo
```
