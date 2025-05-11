# Attribute `@` must be at top level in formation

Since the decorated object is key piece of information in the code,
it's better to keep it at the top level of attributes in formation.
In other words, the `@` attribute shouldn't belong to an object that's
itself an attribute of the formation.
If such structure is needed, it’s better to move the `@`
declaration higher and use the name `@` to create object.

Incorrect:

```eo
# Comment.
[] > app
  foo > x
    bar > @
```

Correct:

```eo
# Comment.
[] > app
  bar > @
  foo > x
    @
```
