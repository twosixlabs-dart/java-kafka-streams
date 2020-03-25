package com.worldmodelers.kafka.processors.java;

import scala.collection.immutable.Stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StreamsApp {

    public static void main(String[] args) throws IOException {

        String configName;

        if (args.length > 0) configName = args[0];
        else configName = "default";

        String configResource = configName.trim() + ".properties";

        Properties properties = new Properties();
        InputStream propsStream = Stream.Cons.class.getClassLoader().getResourceAsStream( configResource );

        properties.load( propsStream );

        ExampleStreamProcessor streamProcessor = new ExampleStreamProcessor( properties );

        streamProcessor.runStreams();

    }

}
