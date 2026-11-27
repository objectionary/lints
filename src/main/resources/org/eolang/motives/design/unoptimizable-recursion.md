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

A self-call outside of a tail position is turned into a loop too, but
in two steps. First the compiler rewrites the formation into
continuation-passing style. The formation gets an extra void, the
continuation, and the work left to do after the call answers moves into
it, so every self-call becomes a tail call. Then the recursion runs
inside `PhLoop`, like the one in the previous example. A self-call under an operation, or in an attribute that φ
leads to, is handled this way:

```eo
[] > app
  [n] > rec
    if. > @
      n.lt 1
      0
      n.plus (^.rec (n.minus 1))
  rec 5 > @
```

Every other shape runs as plain recursion, which is correct but deeper
and slower, and the compiler says nothing about it. This lint says it,
naming the reason it found. The rewrite skips, for example, a formation
whose φ does not lead to the attribute holding the self-call, one that
calls itself from inside a nested formation or from inside the arguments
of another self-call, and one that reaches the self-call through an
`if.` that may not belong to a `bool`:

```eo
[] > app
  [n] > rec
    ^.rec (n.minus 1) > cached
    n.plus 1 > @
  rec 5 > @
```

Here `cached` is never read by φ, so the rewrite cannot move it into
the continuation. Make φ lead to every self-call, or drop the ones it
does not need, and the recursion becomes a loop again.

The compiler recognises the self-call solely when spelled through the receiver,
`^.rec`. A self-call spelled with the bare name or with `$`, even in a
tail position, is never turned into a loop, because the compiler looks
for `xi.rho.<name>` and nothing else:

```eo
[] > calc
  seq > @
    *
      1
      calc (n.minus 1)
```

Mutual recursion is never turned into a loop either, because the compiler
handles only a formation calling itself. Here `even` calls `odd` and `odd`
calls `even` back, so every step takes a Java stack block, and the lint
warns about both formations:

```eo
[] > app
  [n] > even
    if. > @
      n.eq 0
      true
      ^.odd (n.minus 1)
  [n] > odd
    if. > @
      n.eq 0
      false
      ^.even (n.minus 1)
  even 10 > @
```

Merge such formations into one that calls itself, and the recursion
becomes a loop.
