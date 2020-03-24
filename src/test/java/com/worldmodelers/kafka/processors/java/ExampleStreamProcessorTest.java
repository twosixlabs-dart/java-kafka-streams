package com.worldmodelers.kafka.processors.java;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.worldmodelers.kafka.messages.ExampleStreamMessage;
import com.worldmodelers.kafka.messages.ExampleStreamMessageJsonFormat;
import kafka.server.KafkaConfig$;
import net.mguenther.kafka.junit.*;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.*;

public class ExampleStreamProcessorTest extends ExampleStreamMessageJsonFormat {

    private final Logger LOG = LoggerFactory.getLogger( ExampleStreamProcessor.class );

    private EmbeddedKafkaConfig kafkaConfig = EmbeddedKafkaConfig
            .create()
            .with( KafkaConfig$.MODULE$.PortProp(), 6308 )
            .build();

    private EmbeddedKafkaClusterConfig kafkaClusterConfig = EmbeddedKafkaClusterConfig
            .create()
            .provisionWith( kafkaConfig )
            .build();

    private Properties properties = new Properties();

    public ExampleStreamProcessorTest() throws IOException {
        InputStream propStream = getClass().getClassLoader().getResourceAsStream( "test.properties" );
        properties.load( propStream );
        LOG.info("PROPERTIES!:");
        properties.forEach( (k,v) -> {
            LOG.info(k.toString());
        } );
    }

    @Rule
    public EmbeddedKafkaCluster cluster = EmbeddedKafkaCluster.provisionWith( kafkaClusterConfig );

    @Test
    public void ExampleStreamProcessorShouldProcessAMessage() throws JsonProcessingException, InterruptedException {
        ExampleStreamProcessor streamProcessor = new ExampleStreamProcessor( properties );
        String inputTopic = "stream.in";
        String outputTopic = "stream.out";

        ExampleStreamMessage streamMessage = new ExampleStreamMessage( "id1", new ArrayList<String>() );
        String streamMessageJson = marshalMessage( streamMessage );

        streamProcessor.runStreams();

        List<KeyValue<String, String>> records = new ArrayList<>();

        records.add( new KeyValue<>( streamMessage.getId(), streamMessageJson ) );

        SendKeyValues<String, String> sendRequest = SendKeyValues.to( inputTopic, records ).useDefaults();

        cluster.send( sendRequest );

        ObserveKeyValues<String, String> observeRequest = ObserveKeyValues.on(outputTopic, 1).useDefaults();

        List<String> observedValues = cluster.observeValues(observeRequest);

        assertEquals( observedValues.get( 0 ), "{\"id\":\"id1\",\"breadcrumbs\":[\"scala-kafka-streams\"]}" );
    }
}