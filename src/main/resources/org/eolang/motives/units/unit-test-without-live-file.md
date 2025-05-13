# Unit test without live file

Each test file must have the live file, with the same name.

Incorrect:

```text
main/
-----bar.eo
-----...
tests/
-----foo-test.eo
-----...
```

Correct:

```text
main/
-----foo.eo
-----...
tests/
-----foo-test.eo
-----...
```
