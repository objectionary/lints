# Same if branches

Both branches of an `if` should not run the same code. When the branch
taken if the condition is true does exactly the same thing as the branch
taken if it is false, the condition makes no difference to the outcome,
and the duplicated code hides that fact from the reader.

Incorrect:

```eo
[x] > foo
  if. > @
    x.gt 0
    stdout "Boom!"
    stdout "Boom!"
```

Here, `foo` prints `"Boom!"` no matter what `x` is, since both branches
of the `if` are identical. The condition should be removed and the
shared code moved above it:

```eo
[x] > foo
  stdout "Boom!" > @
```
