package com.worldmodelers.kafka.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ExampleStreamMessage {
    @JsonProperty( "id" )
    public String id;

    @JsonProperty( "breadcrumbs" )
    public ArrayList<String> breadcrumbs = new ArrayList<>();

    @JsonCreator
    public ExampleStreamMessage( @JsonProperty( "id" ) String idIn, @JsonProperty( "breadcrumbs" ) ArrayList<String> breadcrumbsIn ) {
        id = idIn;
        breadcrumbs = breadcrumbsIn;
    }
}
