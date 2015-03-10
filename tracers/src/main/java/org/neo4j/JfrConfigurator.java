/**
 * Copyright (c) 2002-2014 "Neo Technology,"
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
package org.neo4j;

import java.io.File;
import java.util.Properties;

import com.jamesmurty.utils.XMLBuilder;
import org.neo4j.jfr.configuration.Event;
import org.neo4j.jfr.configuration.Manifest;
import org.neo4j.jfr.configuration.ManifestReader;
import org.neo4j.jfr.configuration.Producer;

public class JfrConfigurator
{
    public static void main( String[] args ) throws Exception
    {
        Manifest manifest = new ManifestReader().readManifest( "/manifest.txt" );

        File javaHome = new File( System.getProperty( "java.home" ) );
        if (javaHome.toPath().endsWith( "jre" )) javaHome = javaHome.toPath().getParent().toFile();
        File defaultJfcDirectory = new File( javaHome, "jre/lib/jfr" );

        XMLBuilder xmlBuilder = XMLBuilder.parse( new File( defaultJfcDirectory, "default.jfc" ) );

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
        outputProperties.put( "indent", "yes" );
        System.out.println( xmlBuilder.asString( outputProperties ) );

        throw new UnsupportedOperationException( "TODO: read manifest and output xml" );
    }
}
