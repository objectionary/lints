# Anemic Getter

A named object that does nothing but give access to something the code can
already reach at the place where the object sits is an "anemic getter."
Such renaming is redundant, since the original may be used directly, with no
extra name added.

Incorrect:

```eo
# Book.
[title] > book
  title > t
```

Here, `t` is a getter that only gives access to `title`. It adds nothing and
just introduces a second name for the same object.

Correct:

```eo
# Book.
[title] > book
```

Simply use `title` directly wherever `t` was needed. The same applies to any
sibling attribute, not only void ones:

```eo
# Foo.
[] > foo
  42 > x
  x > y
```

Here `y` is an anemic getter for `x` and should be removed.

The formation itself is reachable by `$`, so renaming it is just as
redundant:

```eo
# Foo.
[] > foo
  $ > s
```

Here `s` is an anemic getter for `$`. Write `$` where `s` was used, and drop
it.

The parent object, reachable by `^` (or a longer chain of hops, such as
`^.^`), is different: naming it, as in `^ > f`, is not flagged, even though
`^` already says it. The name survives nesting, where the hop-count spelling
does not — a name written once as `f` stays `f` no matter how deeply the
formation ends up nested, while `^.^` silently retargets to whatever
happens to be two hops up if a block using it is re-indented or wrapped in
another formation.

A reference that goes past the hops for an attribute is not a rename either,
and we leave it alone:

```eo
# Foo.
[] > foo
  ^.bar > f
```

Here `f` is not a second name for `^`, but a name for an attribute of it.
