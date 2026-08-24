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

The parent object is different. It is reachable by `^`, or by a longer chain
of hops such as `^.^`. Naming it, as in `^ > f`, is not flagged, even though
`^` already says it.

A name survives nesting and a count of hops does not. A name written once as
`f` stays `f` wherever the formation ends up. A `^.^` always points two hops
up, so wrapping the block in another formation makes it point elsewhere.

A reference that goes past the hops for an attribute is not a rename either,
and we leave it alone:

```eo
# Foo.
[] > foo
  ^.bar > f
```

Here `f` is not a second name for `^`, but a name for an attribute of it.
