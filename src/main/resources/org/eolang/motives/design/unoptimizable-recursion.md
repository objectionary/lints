# Unoptimizable recursion

A nested formation that calls itself in a tail position is turned into a
Java loop at transpile time: the compiler wraps the copies into `PhLoop`
and the calls into `PhAgain`, so a run takes one object per step instead
of one Java stack block per step.

The compiler recognises two tail positions, a branch of an `if.` and the
last step of a `seq *`:

```eo
[] > app
  [n] > rec
    if. > @
      n.lt 1
      0
      ^.rec (n.minus 1)
  rec 5 > @
```

Every other shape runs as plain recursion, which is correct but deeper
and slower, and the compiler says nothing about it. This lint says it,
naming the reason it found. A self-call under an operation, for example,
is not in a tail position, because the operation has work left to do
after the call answers:

```eo
[] > app
  [n] > rec
    if. > @
      n.lt 1
      0
      n.plus (^.rec (n.minus 1))
  rec 5 > @
```

Rewrite such an object with an accumulator, the way `repeated` was
rewritten by hand, and the recursion becomes a loop again.
