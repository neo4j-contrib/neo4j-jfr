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
package org.neo4j.io.pagecache.tracing.jfr;

import com.oracle.jrockit.jfr.EventDefinition;
import com.oracle.jrockit.jfr.TimedEvent;
import com.oracle.jrockit.jfr.ValueDefinition;

import org.neo4j.io.pagecache.tracing.EvictionEvent;
import org.neo4j.io.pagecache.tracing.EvictionRunEvent;

@EventDefinition(path = "neo4j/io/pagecache/evictionrun")
public class JfrEvictionRunEvent extends TimedEvent implements EvictionRunEvent
{
    static final String REL_KEY_EVICTION_RUN = "http://neo4j.com/jfr/evictionRun";
    private final EvictionEventStarter evictionEventStarter;

    @ValueDefinition(name = "evictionRun", relationKey = REL_KEY_EVICTION_RUN )
    private long evictionRun;
    @ValueDefinition(name = "expectedEvictions")
    private int expectedEvictions;
    @ValueDefinition(name = "actualEvictions")
    private int actualEvictions;

    public JfrEvictionRunEvent( EvictionEventStarter evictionEventStarter )
    {
        super( JfrPageCacheTracer.evictionRunToken );
        this.evictionEventStarter = evictionEventStarter;
    }

    @Override
    public EvictionEvent beginEviction()
    {
        actualEvictions++;
        JfrEvictionEvent evictionEvent = evictionEventStarter.startEviction();
        evictionEvent.setEvictionRun( evictionRun );
        return evictionEvent;
    }

    @Override
    public void close()
    {
        end();
        commit();
    }

    public void setExpectedEvictions( int expectedEvictions )
    {
        this.expectedEvictions = expectedEvictions;
    }

    public int getExpectedEvictions()
    {
        return expectedEvictions;
    }

    public void setEvictionRun( long evictionRun )
    {
        this.evictionRun = evictionRun;
    }

    public long getEvictionRun()
    {
        return evictionRun;
    }

    public int getActualEvictions()
    {
        return actualEvictions;
    }

    public void setActualEvictions( int actualEvictions )
    {
        this.actualEvictions = actualEvictions;
    }
}
