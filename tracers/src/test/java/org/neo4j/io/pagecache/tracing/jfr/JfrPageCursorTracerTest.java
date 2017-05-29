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
package org.neo4j.io.pagecache.tracing.jfr;

import org.junit.Test;

import org.neo4j.io.pagecache.tracing.DummyPageSwapper;
import org.neo4j.io.pagecache.tracing.EvictionEvent;
import org.neo4j.io.pagecache.tracing.PageFaultEvent;
import org.neo4j.io.pagecache.tracing.PinEvent;

import static org.junit.Assert.assertEquals;

public class JfrPageCursorTracerTest
{
    @Test
    public void pageCacheTracerObserveEventsTracedByPageCursorTracer() throws Exception
    {
        JfrPageCursorTracer pageCursorTracer = (JfrPageCursorTracer) JfrPageCursorTracerSupplier.INSTANCE.get();
        JfrPageCacheTracer cacheTracer = new JfrPageCacheTracer();
        pageCursorTracer.init( cacheTracer );

        PinEvent pinEvent = pageCursorTracer.beginPin( true, 1L, new DummyPageSwapper( "dummy", 8192 ) );
        pinEvent.hit();
        pinEvent.done();

        PinEvent pinEventForPageFault = pageCursorTracer.beginPin( true, 1L, new DummyPageSwapper( "dummy", 8192 ) );
        {
            PageFaultEvent pageFaultEvent = pinEventForPageFault.beginPageFault();
            {
                EvictionEvent evictionEvent = pageFaultEvent.beginEviction();
                evictionEvent.close();
            }
            pageFaultEvent.done();
        }
        pinEventForPageFault.done();

        assertEquals( 2, cacheTracer.pins() );
        assertEquals( 2, cacheTracer.unpins() );
        assertEquals( 1, cacheTracer.hits() );
        assertEquals( 1, cacheTracer.faults() );
        assertEquals( 1, cacheTracer.evictions() );
    }
}
