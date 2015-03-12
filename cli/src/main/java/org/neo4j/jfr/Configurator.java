package org.neo4j.jfr;

import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import com.jamesmurty.utils.XMLBuilder2;
import org.neo4j.jfr.configuration.Event;
import org.neo4j.jfr.configuration.Manifest;
import org.neo4j.jfr.configuration.ManifestReader;
import org.neo4j.jfr.configuration.Producer;
import org.xml.sax.InputSource;

public class Configurator
{
    public static void main( String[] args )
    {
        try
        {
            Manifest manifest = new ManifestReader().readManifest( "/manifest.txt" );

            XMLBuilder2 xmlBuilder = getXmlBuilder();

            for ( Producer producer : manifest.getProducers() )
            {
                XMLBuilder2 producerElement = xmlBuilder.xpathFind( "//configuration" ).e( "producer" );
                producerElement.a( "label", "Neo4j" );
                producerElement.a( "uri", producer.getUri() );

                for ( Event event : producer.getEvents() )
                {
                    XMLBuilder2 eventElement = producerElement.e( "event" );
                    eventElement.a( "path", event.getPath() );

                    eventElement.e( "setting" ).a( "name", "enabled" ).t( "true" );
                    eventElement.e( "setting" ).a( "name", "stackTrace" ).t( "true" );
                }
            }

            Properties outputProperties = new Properties();
            outputProperties.put( OutputKeys.INDENT, "yes" );
            outputProperties.put( "{http://xml.apache.org/xslt}indent-amount", "2" );

            System.out.println( xmlBuilder.asString( outputProperties ) );
        }
        catch ( Exception e )
        {
            System.err.println( "Unable to configure Neo4j-JFR." );
            System.err.println( "Reason given: " + e.getMessage() );
            System.exit( 1 );
        }
    }

    private static XMLBuilder2 getXmlBuilder() throws IOException, ParserConfigurationException
    {
        if ( System.in.available() > 0 )
        {
            return XMLBuilder2.parse( new InputSource( System.in ) );
        }

        XMLBuilder2 builder = XMLBuilder2.create( "configuration" );
        builder.a( "description", "Neo4j events" );
        builder.a( "name", "Neo4j Events" );
        builder.a( "provider", "Neo Technology" );
        builder.a( "version", "1.0" );
        return builder;
    }
}
