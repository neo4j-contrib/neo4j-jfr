package org.neo4j.jfr.commands;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;

import com.jamesmurty.utils.XMLBuilder;
import io.airlift.airline.Command;
import io.airlift.airline.Option;
import org.neo4j.jfr.configuration.Event;
import org.neo4j.jfr.configuration.Manifest;
import org.neo4j.jfr.configuration.ManifestReader;
import org.neo4j.jfr.configuration.Producer;
import org.xml.sax.SAXException;

@Command(name = "dump-config", description = "Dump Neo4j-JFR configuration in .jfc (XML) format")
public class DumpConfiguration implements Runnable
{
    @SuppressWarnings("FieldCanBeLocal")
    @Option(name = {"--extend"},
            title = "jfcfile",
            description = "Extend existing .jfc configuration",
            allowedValues = {"default", "profile"})
    private String mode = "plain";

    @Override
    public void run()
    {
        try
        {
            Manifest manifest = new ManifestReader().readManifest( "/manifest.txt" );

            XMLBuilder xmlBuilder = getXmlBuilder();

            for ( Producer producer : manifest.getProducers() )
            {
                XMLBuilder producerElement = xmlBuilder.xpathFind( "//configuration" ).e( "producer" );
                producerElement.a( "label", "Neo4j" );
                producerElement.a( "uri", producer.getUri() );

                for ( Event event : producer.getEvents() )
                {
                    XMLBuilder eventElement = producerElement.e( "event" );
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
            System.err.println( "Unable to write out configuration." );
            System.err.println( "Reason given: " + e.getMessage() );
            System.exit( 1 );
        }
    }

    private XMLBuilder getXmlBuilder() throws ParserConfigurationException, SAXException, IOException
    {
        if ( mode.equals( "plain" ) )
        {
            return plainXml();
        }

        File javaHome = new File( System.getProperty( "java.home" ) );

        if ( javaHome.toPath().endsWith( "jre" ) )
        {
            javaHome = javaHome.toPath().getParent().toFile();
        }

        File defaultJfcDirectory = new File( javaHome, "jre/lib/jfr" );

        File eventSettingsFile = new File( defaultJfcDirectory, format( "%s.jfc", mode ) );

        return XMLBuilder.parse( eventSettingsFile );
    }

    private XMLBuilder plainXml() throws ParserConfigurationException
    {
        XMLBuilder builder = XMLBuilder.create( "configuration" );
        builder.a( "provider", "Neo Technology" );
        builder.a( "description", "Bare bones Neo4j tracers" );
        return builder;
    }
}
