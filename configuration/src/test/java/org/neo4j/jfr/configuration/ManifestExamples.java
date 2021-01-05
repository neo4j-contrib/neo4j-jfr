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

import static java.util.Arrays.asList;

import java.util.HashSet;

public class ManifestExamples
{
    public static Manifest theTestManifest()
    {
        // http://example.com/testing/,some/event,another/event,a/third/event
        // http://example.com/more/testing/,events/keep/coming

        return new Manifest(
                new HashSet<>(
                        asList(
                                new Producer(
                                        "http://example.com/testing/",
                                        new HashSet<>(
                                                asList(
                                                        new Event( "some/event" ),
                                                        new Event( "another/event" ),
                                                        new Event( "a/third/event" )
                                                )
                                        )
                                ),
                                new Producer(
                                        "http://example.com/more/testing/",
                                        new HashSet<>(
                                                asList(
                                                        new Event( "events/keep/coming" )
                                                )
                                        )
                                )
                        )
                )
        );
    }
}
