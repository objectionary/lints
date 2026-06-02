# Too deep object

Objects nested more than 12 levels deep are hard to read and maintain.
Flatten the hierarchy or extract inner objects into separate files.

Incorrect:

```eo
[] > outer
  [] > inner1
    [] > inner2
      ...
        [] > inner12
```

Correct:

```eo
[] > outer
  inner1 > @
[] > inner1
  inner2 > @
```