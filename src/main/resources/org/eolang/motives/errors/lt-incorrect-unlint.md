# Incorrect unlints

The meta part of the object should not contain +unlint point on non-existent
lint, as it is useless

Incorrect:

```eo
+unlint abracadabra
```

Because lint with name "abracadabra" does not exist(perhaps at some
point this lint will be added and this example will become correct :) )

Correct:

```eo
+unlint ascii-only
```

Because lint with name "ascii-only" exist
