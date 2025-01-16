# Abstract Decoratee

Abstract object shouldn't be used as a decoratee.

Incorrect:

```eo
[] > main
  [] > @
    hello > test
```

Correct:

```eo
[args] > main
  foo > @
    hello > test
```
