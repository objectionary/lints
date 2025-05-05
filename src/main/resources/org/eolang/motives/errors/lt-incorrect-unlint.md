# Incorrect unlints

The meta part of the object shouldn't contain +unlint point on non-existent
lint, as it's useless

Incorrect:

```eo
+unlint abracadabra
```

Because lint with name "abracadabra" doesn't exist(perhaps at some
point this lint will be added and this example will become correct :) )

Correct:

```eo
+unlint ascii-only
```

Because lint with name `ascii-only` exist
