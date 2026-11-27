# Anemic Getter

A named object that does nothing but give access to something the code can
already reach at the place where the object sits is an "anemic getter."
Such renaming is redundant, since the original may be used directly, with no
extra name added.

Incorrect:

```eo
# Foo.

[] > foo
  42 > x
  x > y
```

Here, `y` is a getter that gives access to `x` and nothing else. It adds
nothing and just introduces a second name for the same object.

Correct:

```eo
# Foo.

[] > foo
  42 > x
```

Use `x` directly wherever `y` was needed.

A void attribute is different, though.
It can be filled from outside, but never read from there,
so a getter is the only way to expose it:

```eo
# Book.

[title] > book
  title > t
```

Here `t` is not anemic, since `book.title` can't be written,
while `book.t` can.

The parent object and the formation itself are reachable by `^` and `$`, so
renaming them is just as redundant:

```eo
# Foo.

[] > foo
  ^ > f
  $ > s
```

Here `f` is an anemic getter for `^`, and `s` is one for `$`. Write `^` and
`$` where `f` and `s` were used, and drop both. A longer chain of hops, such
as `^.^`, is no different.

A reference that goes past the hops for an attribute, though, is not a
rename, and we leave it alone:

```eo
# Foo.

[] > foo
  ^.bar > f
```

Here `f` is not a second name for `^`, but a name for an attribute of it.
