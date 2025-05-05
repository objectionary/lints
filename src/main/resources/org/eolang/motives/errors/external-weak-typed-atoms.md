# External weak typed atoms

Weak typing `/?` doesn't allowed outside of `org.eolang` package.

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
