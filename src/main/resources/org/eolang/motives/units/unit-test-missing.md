# Unit Test Missing

Each live file should have unit test file, with the same name.

Incorrect:

```text
main/
-----foo.eo
-----...
tests/
-----bar-tests.eo
-----...
```

Correct:

```text
main/
-----foo.eo
-----...
tests/
-----foo-tests.eo
-----...
```
