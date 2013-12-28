Fanciful
========
Lightweight library offering pleasant chat message formatting for Bukkit plugins. A way to get at the good stuff offered by Minecraft 1.7's new chat protocol without dropping down to raw JSON.

Installation
--------
Use Maven. Add the Fanciful repository and dependency entries to your `pom.xml`.

    <repository>
      <id>fanciful-mvn-repo</id>
      <url>https://raw.github.com/mkremins/fanciful/mvn-repo/</url>
      <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
      </snapshots>
    </repository>

    <dependency>
      <groupId>mkremins</groupId>
      <artifactId>fanciful</artifactId>
      <version>0.1.1</version>
    </dependency>

Usage
--------
See [Example.java](http://github.com/mkremins/fanciful/tree/master/src/example/java/mkremins/fanciful/Example.java) for a simple example.

Status
--------
Super-duper WIP. Fortunately, Bukkit 1.7.x is no more ready for release than this is, so I've got some time.

License
--------
[MIT License](http://opensource.org/licenses/MIT). Hack away.
