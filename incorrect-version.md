# Incorrect `+version`

Tail of the special `+version` meta must follow [SemVer] format.

Incorrect:

```eo
+version 0.0
+version 123
+version 1. 1 .1
+version 1.1.alpha
+version 1-alpha
+version alpha.1.1

[] > foo
```

Correct:

```eo
+version 0.0.0
+version 0.2.1
+version 42.0.555555
+version 0.0.1-alpha
+version 0.0.1-beta-1

[] > foo
```

[SemVer]: https://semver.org/
