# Incorrect unlints

The meta part of the object should not contain +unlint directives for non-existent
lints, as they are useless.

Incorrect:

```eo
+unlint abracadabra
```

Because a lint named "abracadabra" doesn't exist (perhaps at some
point this lint will be added and this example will become correct).

Correct:

```eo
+unlint ascii-only
```

Because a lint named `ascii-only` exists.
