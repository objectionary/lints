# Duplicate Aliases

Object's aliases must not be duplicated.

Incorrect:

```eo
+alias stdin org.eolang.io.stdin
+alias stdout org.eolang.io.stdout
+alias stdout org.eolang.io.stdout

# Print text to stdout.
[] > main
  (stdout "Hello, world!").print
```

Correct:

```eo
+alias stdin org.eolang.io.stdin
+alias stdout org.eolang.io.stdout

# Print text to stdout.
[] > main
  (stdout "Hello, world!").print
```
