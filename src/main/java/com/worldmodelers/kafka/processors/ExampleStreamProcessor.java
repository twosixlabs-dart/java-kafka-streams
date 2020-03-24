package com.worldmodelers.kafka.processors;

import com.worldmodelers.kafka.messages.ExampleStreamMessage;
import com.worldmodelers.kafka.messages.serde.ExampleStreamMessageSerde;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Produced;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ExampleStreamProcessor {

    private final Logger LOG = LoggerFactory.getLogger( ExampleStreamProcessor.class );
    private Properties kafkaProps;

    public ExampleStreamProcessor( Properties properties ) {
        properties.forEach( ( key, val ) -> {
            if ( key.toString().startsWith( "kafka" ) ) {
                kafkaProps.put( key.toString().substring( 5 ), val );
            }
        } );
    }

    private Serde<String> stringSerdes = Serdes.String();
    private Serde<ExampleStreamMessage> streamMessageSerdes =new ExampleStreamMessageSerde;

    implicit val
    consumed :Consumed[String,ExampleStreamMessage ]=Consumed .`with`(stringSerdes,streamMessageSerdes )
    implicit val
    produced :Produced[String,ExampleStreamMessage ]=Produced .`with`(stringSerdes,streamMessageSerdes )

    private Topology buildStream() {
        StreamsBuilder builder = new StreamsBuilder();
        builder.stream( "stream.in" ).mapValues( (message_ -> {
                message.copy( breadcrumbs = message.breadcrumbs :+"scala-kafka-streams" )
        } ).to( "stream.out" )

        builder.build( kafkaProps )
    }

    def topics() :Seq[String ]=

    Seq( "stream.in","stream.out" )

    public Void runStreams() {
        Topology topology = buildStream();
        val streams = new KafkaStreams( topology, kafkaProps )

        streams.start()
        sys.addShutdownHook( streams.close( Duration.ofSeconds( 10 ) ) )
    }

}
