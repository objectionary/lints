# External Weak Typed Atoms

Weak typing `/?` does not allowed outside of `org.eolang` package.

Incorrect:

```eo
+package my.awesome.package

[] > my-awesome-object /?
```

Correct:

```eo
+package my.awesome.package

[] > my-awesome-object /int
```
