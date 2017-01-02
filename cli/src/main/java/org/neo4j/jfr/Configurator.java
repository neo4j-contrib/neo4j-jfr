/**
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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

        XMLBuilder2 configuration = XMLBuilder2.create( "configuration" );
        configuration.a( "description", "Neo4j events" );
        configuration.a( "name", "Neo4j Events" );
        configuration.a( "provider", "Neo Technology" );
        configuration.a( "version", "1.0" );

        XMLBuilder2 producer = configuration.e( "producer" );
        producer.a( "uri", "http://www.oracle.com/hotspot/jfr-info/" );
        producer.a( "label", "Oracle JDK" );

        XMLBuilder2 recordingEvent = producer.e( "event" );
        recordingEvent.a( "path", "recordings/recording" );
        recordingEvent.e( "setting" ).a( "name", "enabled" ).t( "true" );

        XMLBuilder2 recordingSettingEvent = producer.e( "event" );
        recordingSettingEvent.a( "path", "recordings/recording_setting" );
        recordingSettingEvent.e( "setting" ).a( "name", "enabled" ).t( "true" );

        return configuration;
    }
}
