# Incorrect `+rt` node location

The Node.js runtime location must follow this regular expression:

```regexp
^([a-zA-Z0-9_.-]+):(\d+\.\d+\.\d+)$
```

Incorrect:

```eo
# No comments.

+rt node foo
+rt node hello, world!
+rt node bar-test:test:1.2.3

[] > foo
```

Correct:

```eo
# No comments.

+rt node eo2js-runtime:0.0.0
+rt node bar-test:1.1.0
+rt node foo:1.2.3

[] > foo
```
