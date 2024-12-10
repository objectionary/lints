# Incorrect test object name

Test object name must follow regexp:

```regexp
^[a-z][a-z]+(-[a-z][a-z]+)*$
```

Incorrect:

```eo
+tests

# Test.
[] > tEst
  42 > foo

# Test.
[] > test123
  42 > fooB

# Test.
[] > test--
  42 > foo

# Test.
[] > test--test
  42 > foo

# Test.
[] > t
  42 > foo

# Test.
[] > test-test-
  42 > foo

# Test.
[] > test-Test
  42 > foo

# Test.
[] > test_abc
  42 > foo
```

Correct:

```eo
+tests

# Test.
[] > runs
  42 > foo
  
# Test.
[] > runs-something
  42 > fooB
  
# Test.
[] > good-one
  42 > foo
  
# Test.
[] > ok
  42 > foo
```
