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

# Global Ban Handler

Global Ban Hander can be used to detect when the ban listener has banned a member. You can create one by implementing the GlobalBanHandler interface.

```java
public BanHandler implements GlobalBanHandler{
  
  @Override
  public void onGlobalBanEvent(GlobalBanEvent event){
      System.out.println("User " + event.getBanInfo().getEffectiveName() + " was banned in " + event.getGuild().getName());
  }
  @Override
  public void onBannedUserJoin(BannedUserJoinEvent event){
      System.out.println("Globally banned user " + event.getBanInfo() + " joined the guild " + event.getGuild().getName());
  }
}
```
Next, register this listener:
```java
banListener.addHandler(new BanHandler());
```

Both `GlobalBanEvent` and `BannedUserJoinEvent` have accessors for the JDA `GuildMemberJoinEvent` fields
