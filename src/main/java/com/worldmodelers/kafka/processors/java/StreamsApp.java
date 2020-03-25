package com.worldmodelers.kafka.processors.java;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class StreamsApp {

    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        InputStream propsStream = StreamsApp.class.getClassLoader().getResourceAsStream( "app.properties" );
        properties.load( propsStream );

        ExampleStreamProcessor streamProcessor = new ExampleStreamProcessor( properties );

        streamProcessor.runStreams();

    }

}
