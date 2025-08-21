# Unused alias

All defined object aliases must be used.

Incorrect:

```eo
+alias err org.eolang.io.stderr
+alias in org.eolang.io.stdin
+alias org.eolang.io.stdout

# Foo.
[x] > foo
  x.div in.nextInt > @
```

Correct:

```eo
+alias in org.eolang.io.stdin

# Foo.
[x] > foo
  x.div in.nextInt > @
```
