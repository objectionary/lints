# Unused Alias

All defined object's aliases must be in use.

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
