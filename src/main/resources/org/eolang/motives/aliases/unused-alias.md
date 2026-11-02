# Unused alias

All defined object aliases must be used.

Incorrect:

```eo
# Foo.

+alias err org.eolang.io.stderr
+alias in org.eolang.io.stdin
+alias org.eolang.io.stdout

[x] > foo
  x.div in.nextInt > @
```

Correct:

```eo
# Foo.

+alias in org.eolang.io.stdin

[x] > foo
  x.div in.nextInt > @
```
