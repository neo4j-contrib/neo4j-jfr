/**
 * Copyright (c) 2002-2021 "Neo Technology,"
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
package org.neo4j.jfr.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import org.apache.commons.io.IOUtils;

public class ManifestReader
{
    public Manifest readManifest( String name ) throws IOException
    {
        InputStream inputStream = getClass().getResourceAsStream( name );

        HashSet<Producer> producers = new HashSet<>();

        for ( String line : IOUtils.readLines( inputStream ) )
        {
            String[] uriAndPaths = line.split( ",", 2 );

            String uri = uriAndPaths[0];
            String paths = uriAndPaths[1];

            HashSet<Event> events = new HashSet<>();

            for ( String path : paths.split( "," ) )
            {
                events.add( new Event( path ) );
            }

            producers.add( new Producer( uri, events ) );
        }

        return new Manifest( producers );
    }
}
