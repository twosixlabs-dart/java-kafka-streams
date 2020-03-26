import com.worldmodelers.kafka.processors.java.ExampleStreamProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main {

    public static void main( String[] args ) throws IOException {

        Properties properties = new Properties();
        InputStream propsStream = Main.class.getClassLoader().getResourceAsStream( String.format( "%s.properties", args[ 0 ] ) );

        properties.load( propsStream );

        String topicFrom = properties.getProperty( "topic.from" );
        String topicTo = properties.getProperty( "topic.to" );

        ExampleStreamProcessor streamProcessor = new ExampleStreamProcessor( topicFrom, topicTo, properties );

        streamProcessor.runStreams();

    }

}
