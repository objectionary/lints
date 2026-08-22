# Redundant attachment

The `>>` suffix gives an anonymous formation a name generated for it, so
that it can be referenced from elsewhere: by itself recursively (via `%`),
or by another object. If nothing ever refers to that generated
name, the `>>` is redundant and should be removed.

Incorrect:

```eo
[] > foo
  test > @
    [] >>
      2.plus 2 > @
```

Correct:

```eo
[] > foo
  test > @
    []
      2.plus 2 > @
```

A generated name is *not* redundant when it is actually used,
either by a recursive self-reference:

```eo
[] > foo
  test > @
    [n] >>
      if. > @
        n.eq 0
        0
        % (n.minus 1)
```

or by being called from another part of the same object:

```eo
[] > foo
  [n] >> helper
    n.plus 1 > @
  helper 5 > @
```

The name is not redundant either when the body reads objects from the scope
that encloses it, beyond its own voids. Such an object cannot become
anonymous, because `anonymous-formation` forbids an anonymous formation from
reaching outside itself. Here `func` comes from `mapped`, not from
`[item idx]`, so the `>>` stays:

```eo
[sequence func] > mapped
  sequence.mapped > @
    func item > [item idx] >>
```

A generated name that the parser invents on its own, rather than for a `>>`
written in the source, is left alone too. The `!` suffix on a nameless
argument is such a case: `m.plus m!` makes the parser wrap `m` into a named
`.as-bytes` over `Φ.dataized`, and no `>>` exists in the source to be
removed.
