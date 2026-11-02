# Incorrect test object name

Test object names must follow this regular expression:

```regexp
^[a-z][a-z0-9]*(-[a-z0-9]+)*$
```

Incorrect:

```eo
# Foo.

[] > foo

  [] +> tEst
    42 > foo

  [] +> test--
    42 > foo

  [] +> test--test
    42 > foo

  [] +> test-test-
    42 > foo

  [] +> test-Test
    42 > foo

  [] +> test_abc
    42 > foo
```

Correct:

```eo
# Foo.

[] > foo

  [] +> runs
    42 > foo

  [] +> runs-something
    42 > fooB

  [] +> good-one
    42 > foo

  [] +> ok
    42 > foo

  [] +> t
    42 > foo

  [] +> runs-i32
    42 > foo
```
