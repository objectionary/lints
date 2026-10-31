# Misplaced dataization

The cache of a `!` lives in the formation that holds it. When a nested
formation dataizes an object of the formation around it, every copy of the
nested formation starts with an empty cache and dataizes the same object
again. A recursive formation makes a new copy on every step, so the
delimiter below is dataized once per step, though it never changes:

```eo
[^ items] > joined
  [^ acc tup] >> with-delimiter
    if. > @
      tup.length.eq 0
      acc
      ^.with-delimiter
        concat.
          concat. (dataized tup.head) ^.^!
          acc
        tup.tail
```

The lint warns when two things hold. First, the target of a `!` or of a
`dataized` is a plain chain of attribute reads that goes through `^`.
Second, the formation that holds it sits inside another formation of the
same file. Move the dataization up into the enclosing formation, where it
runs once per call:

```eo
[^ items] > joined
  ^ >> delimiter!
  [^ acc tup] >> with-delimiter
    if. > @
      tup.length.eq 0
      acc
      ^.with-delimiter
        concat.
          concat. (dataized tup.head) ^.delimiter
          acc
        tup.tail
```

The lint stays silent on an application, such as `^.sys.accept >>!` with
its arguments, because a syscall must run on every call, and moving it up
would run it only once. It also stays silent on a global, such as `eol!`,
on a top-level formation, whose enclosing object lives in another file,
and inside a test.
