# Noname Attributes

Each object's attribute must have a name.

Incorrect:

```eo
[] > foo
  first
  second
```

```eo
[args] > main
  (stdout "Hello!").print
```

Correct:

```eo
[] > foo
  first > hi
  second > hey
```

```eo
[args] > main
  (stdout "Hello!").print > out
```
