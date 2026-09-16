# Incorrect test object name

Test object names must follow this regular expression:

```regexp
^[a-z][a-z]+(-[a-z][a-z]+)*$
```

Incorrect:

```eo
# Foo.

[] > foo

  [] +> tEst
    42 > foo

  [] +> test123
    42 > fooB

  [] +> test--
    42 > foo

  [] +> test--test
    42 > foo

  [] +> t
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
```
