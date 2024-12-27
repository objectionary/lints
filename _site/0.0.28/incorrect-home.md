# Incorrect `+home`

Special meta `+home` must follow regex:

```regexp
^(?:http(s)?://)?[\w.-]+(?:\.[\w.-]+)+[\w\-._~:/?#\[\]@!$&'()*+,;=]+$
```

Incorrect:

```eo
+home url

[] > foo
```

Correct:

```eo
+home https://github.com/objectionary/eo
+home 255.255.255.255

[] > foo
```
