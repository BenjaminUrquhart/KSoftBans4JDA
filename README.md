# KSoftBans4JDA

A simple way of adding global ban functinality to any JDA bot!

# How to use:

1. Create a BanListener object:
```java
import net.benjaminurquhart.ksoftbans4jda.*;
...
BanListener banListener = new BanListener("KSoft Token", true);
```

2. Register the listener:
```java
jda.addEventListener(banListener);
```

That's it!
