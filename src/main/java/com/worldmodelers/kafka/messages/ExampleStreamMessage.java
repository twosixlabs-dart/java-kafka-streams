package com.worldmodelers.kafka.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExampleStreamMessage extends Serializable {
    private static long serialVersionUID = 2L;

    @JsonProperty( "id" )
    private String id;

    @JsonProperty( "breadcrumbs" )
    private List<String> breadcrumbs;


    @JsonCreator
    public ExampleStreamMessage( @JsonProperty( "id" ) String id, @JsonProperty( "breadcrumbs" ) List<String> breadcrumbs ) {
        this.id = id;
        if ( breadcrumbs == null ) {
            this.breadcrumbs = new ArrayList<>();
        } else {
            this.breadcrumbs = breadcrumbs;
        }
    }

    public ExampleStreamMessage() {
        this.id = null;
        this.breadcrumbs = null;
    }

    public void setId( String idIn ) {
        id = idIn;
    }

    public String getId() {
        return id;
    }

    public void setBreadcrumbs( ArrayList<String> breadcrumbsIn ) {
        breadcrumbs = breadcrumbsIn;
    }

    public List<String> getBreadcrumbs() {
        return breadcrumbs;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }
        ExampleStreamMessage message = (ExampleStreamMessage) o;
        return Objects.equals( id, message.id ) &&
                Objects.equals( breadcrumbs, message.breadcrumbs );
    }

    @Override
    public int hashCode() {
        return Objects.hash( id, breadcrumbs );
    }
}
