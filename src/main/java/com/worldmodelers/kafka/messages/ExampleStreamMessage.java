package com.worldmodelers.kafka.messages;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ExampleStreamMessage {
    @JsonProperty
    public String id;

    @JsonProperty
    public List<String> breadcrumbs;

    public void ExampleStreamMessage( String idIn, List<String> breadcrumbsIn ) {
        id = idIn;
        breadcrumbs = breadcrumbsIn;
    }
}
