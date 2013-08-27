image-maven-plugin
==================

Scale images goal


Usage
==================

    <build>
        <plugins>
            ...
            <plugin>
                <groupId>com.filmon.maven</groupId>
                <artifactId>maven-image-plugin</artifactId>
                <version>1.0</version>
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
