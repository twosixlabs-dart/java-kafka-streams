package com.worldmodelers.kafka.processors.java;

import com.worldmodelers.kafka.messages.ExampleStreamMessage;
import com.worldmodelers.kafka.messages.serdes.ExampleStreamMessageSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

public class ExampleStreamProcessor {

    private final Logger LOG = LoggerFactory.getLogger( ExampleStreamProcessor.class );
    private Properties kafkaProps = new Properties();

    // Constructor retrieves kafka-specific properties from system or application
    // properties prefixed with "kafka." See test.properties in the test resources
    // directory for an example
    public ExampleStreamProcessor( Properties properties ) {
        properties.forEach( ( key, val ) -> {
            if ( key.toString().startsWith( "kafka" ) ) {
                kafkaProps.put( key.toString().substring( 6 ), val );
            }
        } );

        LOG.info( "FIXED PROPERTIES!:" );
        kafkaProps.forEach( ( k, v ) -> {
            LOG.info( k.toString() );
        } );
    }

    // Serdes are objects that handle serializing and deserializing kafka messages
    // one is needed to serialize/deserialize the key (simple string serde) and another
    // to serialize/deserialize the message itself (in this case the custom ExampleStreamMessageSerde
    // defined in the messages package
    private Serde<String> stringSerdes = Serdes.String();
    private Serde<ExampleStreamMessage> streamMessageSerdes = new ExampleStreamMessageSerde();

    // The Topology object, created by StreamsBuilder, defines the flow of messages from
    // an input topic to output topics. The mapValues method is where you define a transformation
    // on the consumed message, and is where you plug in your application's logic.
    private Topology buildStream() {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream( "stream.in", Consumed.with( stringSerdes, streamMessageSerdes ) ).mapValues( ( message ) -> {
            message.getBreadcrumbs().add( "scala-kafka-streams" );
            return message;
        } ).to( "stream.out", Produced.with( stringSerdes, streamMessageSerdes ) );

        return builder.build( kafkaProps );
    }

    // Builds the stream, sets error handling, activates processing, and ensures that
    // the stream will close when the application shuts down
    public void runStreams() {
        Topology topology = buildStream();
        KafkaStreams streams = new KafkaStreams( topology, kafkaProps );

        streams.setUncaughtExceptionHandler( ( Thread thread, Throwable throwable ) -> {
            LOG.error( throwable.getMessage() );
        } );

        streams.start();

        Runtime.getRuntime().addShutdownHook( new Thread( () -> {
            streams.close( Duration.ofSeconds( 10 ) );
        } ) );
    }

}
