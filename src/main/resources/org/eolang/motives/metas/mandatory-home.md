# Mandatory `+home` Meta

The program must have exactly one `+home` special meta.

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
