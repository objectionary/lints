# Duplicate Names

Object's name must not be duplicated.

Incorrect:

```eo
foo
  1 > name
  2 > name
```

```eo
[x] > first
  second > x

18 > first
```

Correct:

```eo
foo
  1 > one
  2 > two
```

```eo
[x] > first
  second > x

18 > age
```
