# Anemic Getter

A named object that does nothing but give access to a sibling attribute of the
same formation is an "anemic getter." Such renaming is redundant, since the
original attribute may be used directly, with no extra name added.

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
