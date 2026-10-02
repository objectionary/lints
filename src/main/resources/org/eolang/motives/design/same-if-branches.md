# Same if branches

Both branches of an `if` should not run the same code. When the true
branch and the false branch do the same thing, the condition makes no
difference to the outcome. The duplicated code also hides that fact
from the reader.

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
shared code moved before it:

```eo
[x] > foo
  stdout "Boom!" > @
```
