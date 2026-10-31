# Incorrect `+package`

The special meta `+package` must follow this regular expression:

```regexp
^[a-zA-Z0-9_]+(\.[a-zA-Z0-9_]+|\$[a-zA-Z0-9_]*)*$
```

Segments written in any alphabet are also accepted, with digits, `_`, `@` and
dashes (a dash can't start a segment), as long as a single dot separates them.

Incorrect:

```eo
+package test.
+package привет, как дела?

[] > foo
```

Correct:

```eo
+package foo.bar
+package my.awesome.package
+package test.xy
+package foo
+package Hello.WorLd
+package привет.как

[] > foo
```
