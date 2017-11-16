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
package org.neo4j.kernel.monitoring.tracing.jfr;

import org.junit.Test;

import org.neo4j.io.pagecache.tracing.PageCacheTracer;
import org.neo4j.io.pagecache.tracing.cursor.PageCursorTracerSupplier;
import org.neo4j.io.pagecache.tracing.jfr.JfrPageCacheTracer;
import org.neo4j.io.pagecache.tracing.jfr.JfrPageCursorTracerSupplier;
import org.neo4j.kernel.impl.util.Neo4jJobScheduler;
import org.neo4j.kernel.monitoring.Monitors;
import org.neo4j.logging.FormattedLog;
import org.neo4j.time.Clocks;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

public class JfrTracerFactoryTest
{
    @Test
    public void createPageCacheTracer() throws Exception
    {
        PageCacheTracer pageCacheTracer = getTracerFactory().createPageCacheTracer( getMonitors(),
                getJobScheduler(), Clocks.nanoClock(), FormattedLog.toOutputStream( System.out ) );
        assertThat( pageCacheTracer, instanceOf( JfrPageCacheTracer.class ) );
    }

    @Test
    public void createPageCursorTracerSupplier() throws Exception
    {
        PageCursorTracerSupplier tracerSupplier =
                getTracerFactory().createPageCursorTracerSupplier( getMonitors(), getJobScheduler() );
        assertThat( tracerSupplier, instanceOf( JfrPageCursorTracerSupplier.class) );
    }

    private Monitors getMonitors()
    {
        return new Monitors();
    }

    private Neo4jJobScheduler getJobScheduler()
    {
        return new Neo4jJobScheduler();
    }

    private JfrTracerFactory getTracerFactory()
    {
        return new JfrTracerFactory();
    }

}
