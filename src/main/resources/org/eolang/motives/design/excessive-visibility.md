# Excessive visibility

Public methods should be exercised by a unit test. If a method is only an
implementation detail and no test refers to it, make it private with the
`>>` decoration instead of exposing it as part of the object's public API.

Incorrect:

```eo
[text] > phrase
  text.index-of " " > position

  [] +> checks-phrase
    phrase "Object Thinking" > @
```

`position` is public but the test never refers to it. It should be written as
`text.index-of " " >> position`.
