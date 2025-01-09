# Incorrect `+architect`

Architect's email must follow regex:

```regexp
^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$
```

Incorrect:

```eo
+architect hello
+architect hello@mail
+architect someone@@
+architect @gmail.com
+architect someone24@.com

[] > foo
```

Correct:

```eo
+architect hello@gmail.com

[] > foo
```
