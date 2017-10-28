Fanciful [![Build Status](https://travis-ci.org/mkremins/fanciful.svg?branch=master)](https://travis-ci.org/mkremins/fanciful)
========
Lightweight library offering pleasant chat message formatting for Bukkit plugins. A way to get at the good stuff offered by Minecraft 1.7's new chat protocol without dropping down to raw JSON.

Installation
--------
Use Maven. Add the Fanciful dependency entry to your `pom.xml`.

```xml
<dependency>
  <groupId>mkremins</groupId>
  <artifactId>fanciful</artifactId>
  <version>0.4.0-SNAPSHOT</version>
</dependency>
```

As of [October 2017](https://github.com/mkremins/fanciful/issues/83), the Maven repository that formerly hosted Fanciful artifacts has been shut down. You could continue using Fanciful by cloning this GitHub repository, building Fanciful as a JAR, and [installing it locally](http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html), but this is not recommended unless you know what you're doing.

Usage
--------
See [Example.java](http://github.com/mkremins/fanciful/tree/master/src/example/java/mkremins/fanciful/Example.java) for a simple example.

Status
--------
Outdated, and largely superseded by newer libraries. No new development or ongoing support. If you're still using Fanciful or looking for something like it, consider one of the following Fanciful-inspired alternatives:

* Spigot's [ChatComponent API](https://www.spigotmc.org/wiki/the-chat-component-api/)
* [KyoriPowered/text](https://github.com/KyoriPowered/text)

License
--------
[MIT License](http://opensource.org/licenses/MIT). Hack away.
