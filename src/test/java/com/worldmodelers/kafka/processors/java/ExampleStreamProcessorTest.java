package com.worldmodelers.kafka.processors.java;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.worldmodelers.kafka.messages.ExampleStreamMessage;
import com.worldmodelers.kafka.messages.ExampleStreamMessageJsonFormat;
import kafka.server.KafkaConfig$;
import net.mguenther.kafka.junit.*;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class ExampleStreamProcessorTest extends ExampleStreamMessageJsonFormat {

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
    }

    @Rule
    public EmbeddedKafkaCluster cluster = EmbeddedKafkaCluster.provisionWith( kafkaClusterConfig );

    @Test
    public void ExampleStreamProcessorShouldProcessAMessage() throws JsonProcessingException, InterruptedException {
        String inputTopic = "stream.in";
        String outputTopic = "stream.out";

        ExampleStreamProcessor streamProcessor = new ExampleStreamProcessor( inputTopic, outputTopic, properties );

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