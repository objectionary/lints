# Excessive visibility

A method that no unit test refers to is not part of the object's
public interface: nothing outside the object depends on it being callable
from outside. Such a method should be declared private (obfuscated with
`>>`) instead of public (`>`), so its scope matches how the method actually gets used.

Incorrect:

```eo
[t] > phrase
  pos.gte 0 > multi-words
  t.index-of " " > pos

  [] +> checks-phrase
    (phrase "Object Thinking").multi-words > @
```

Here, `pos` is never referred to by `checks-phrase` or by any other test, so
it should be private:

```eo
[t] > phrase
  pos.gte 0 > multi-words
  t.index-of " " >> pos

  [] +> checks-phrase
    (phrase "Object Thinking").multi-words > @
```
