# Missed obfuscation

A formation that decorates another object should obfuscate its void
attributes. Otherwise, an attribute of the formation may shadow an attribute
with the same name in the decoratee, making attribute access ambiguous to a
reader.

Incorrect:

```eo
[a b] > x
  foo > @
```

The expression `x.a` refers to the provided attribute `a`, even when `foo`
also has an attribute named `a`. Obfuscating the void attributes makes this
relationship explicit:

```eo
[] > x
  ? >> a
  ? >> b
  foo > @
```

Void attributes whose names start with `cant-` are capabilities or
restrictions and don't have to be obfuscated.
