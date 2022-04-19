---
id: developers
title: Developers
slug: /developers
---

## Maven & Gradle

HexNicks is hosted in Majekdor's Maven Repository [here](https://repo.majek.dev).

### Maven

```xml
<repositories>
    <repository>
        <id>majek-repo</id>
        <url>https://repo.majek.dev/releases/</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>dev.majek</groupId>
        <artifactId>hexnicks</artifactId>
        <version>3.0.0</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Gradle

```groovy
repositories {
    maven {
        url "https://repo.majek.dev/releases/"
    }
}

dependencies {
    compileOnly "dev.majek:hexnicks:3.0.0"
}
```

## API

HexNicks does have an api and all commands trigger an event when executed. These events can be listened to the same way 
as other Bukkit events. You can see the events [here](https://hexnicks.majek.dev/javadoc/dev/majek/hexnicks/api/package-summary.html) 
and all JavaDocs [here](https://hexnicks.majek.dev/javadoc).

Event example:
```java
@EventHandler
public void onNickname(SetNickEvent event) {
  Player player = event.player();
  player.sendMessage("Setting nickname...");
  event.newNick(Component.text("New nickname"));
}
```

There are multiple ways to retrieve nicknames, but the easiest way is:
```java
Nicks.api().getStoredNick(player); // You can pass thru a player, offlineplayer, or uuid
```