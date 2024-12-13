# Signed Binding Indexes

Binding index of application must not be a signed number.

Incorrect:

```eo
object c:-1 d:-2 a:0 b:1 e:+12
```

Correct:

```eo
object c:1 d:2 a:0 b:1 e:12
```
