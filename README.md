image-maven-plugin
==================

Scale and crop images goals


Usage
==================

```xml
<build>
    <plugins>
        ...
        <plugin>
            <groupId>com.filmon.maven</groupId>
            <artifactId>image-maven-plugin</artifactId>
            <version>1.2</version>
            <executions>
                <execution>
                    <goals>
                        <goal>scale</goal>
                    </goals>
                    <configuration>
                        <images>
                            <image>
                                <source>in.png</source>
                                <destination>out-150.png</destination>
                                <width>150</width>
                            </image>
                            <image>
                                <source>in.png</source>
                                <destination>out-300.png</destination>
                                <width>300</width>
                            </image>
                            <image>
                                <source>in.png</source>
                                <destination>out-100.png</destination>
                                <width>100</width>
                            </image>
                        </images>
                    </configuration>
                </execution>
            </executions>
        </plugin>
        ...
    </plugins>
</build>
```

In addition to scaling, it is possible to add black padding around images 
and crop them to specified size. Useful to when generating initial 
image for iPhone or iPad:

```xml
<goals>
    <goal>scale</goal>
    <goal>crop</goal>
</goals>
<configuration>
    <images>
        <image>
            <source>src/main/icons/launcher.png</source>
            <destination>Default@2x.png</destination>
            <width>240</width>
            <cropWidth>640</cropWidth>
            <cropHeight>960</cropHeight>
            <color>black</color>
        </image>
    </images>
</configuration>
```

The color can be specified by name of one fields of java.awt.Color,
or by integer value: `0xffff00` is the same as `yellow`.
