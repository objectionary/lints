# Test with comment

A test object must not have a comment before it. A comment usually just
duplicates the test name: if the test is named `checks-the-app`, a comment
like "This test checks the app" adds nothing. Instead of documenting a
test, make its name short and self-explanatory.

Incorrect:

```eo
# This test checks the app.

[] +> checks-the-app
  42 > @
```

Correct:

```eo
[] +> checks-the-app
  42 > @
```

The name says it all; the comment is redundant.
