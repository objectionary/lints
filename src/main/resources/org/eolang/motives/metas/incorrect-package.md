# Incorrect `+package`

The special meta `+package` must follow this regular expression:

```regexp
^[a-zA-Z0-9_]+(\.[a-zA-Z0-9_]+|\$[a-zA-Z0-9_]*)*$
```

Segments written in any alphabet are also accepted, with digits, `_`, `@` and
dashes (a dash can't start a segment), as long as a single dot separates them.

Incorrect:

```eo
+package foo!

[] > foo
```

```eo
+package привет,

[] > foo
```

Correct:

```eo
+package my.awesome.package

[] > foo
```

```eo
+package Hello.WorLd

[] > foo
```

```eo
+package привет.как

[] > foo
```
