# Mandatory `+home` Meta

Program should have only one `+home` special meta.

Incorrect:

```eo
[] > foo
```

```eo
+home https://github.com/objectionary/eo
+home https://github.com/objectionary/eolang

[] > foo
```

Correct:

```eo
+home https://github.com/objectionary/eo

[] > foo
```
