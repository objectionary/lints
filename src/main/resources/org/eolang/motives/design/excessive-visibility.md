# Excessive visibility

A method that no unit test refers to is not part of the object's
public interface: nothing outside the object depends on it being callable
from outside. Such a method should be declared private (obfuscated with
`>>`) instead of public (`>`), so its scope matches how it is actually used.

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

A number, string, byte array, boolean or tuple bound directly to the
attribute is a constant, not a method, so it is never reported even when no
test refers to it: there is nothing to call on it, and other files may
legitimately read it as published data.

```eo
[] > win32
  8 > append
  256 > creat

  [] +> checks-win32
    42 > @
```
