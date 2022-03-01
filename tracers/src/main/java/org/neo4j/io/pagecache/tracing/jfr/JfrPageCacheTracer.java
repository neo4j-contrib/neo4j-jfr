/**
 * Copyright (c) 2002-2022 "Neo Technology,"
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

import com.oracle.jrockit.jfr.EventToken;
import com.oracle.jrockit.jfr.InstantEvent;
import com.oracle.jrockit.jfr.Producer;

import java.io.File;
import java.net.URI;
import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.helpers.MathUtil;
import org.neo4j.io.pagecache.PageSwapper;
import org.neo4j.io.pagecache.tracing.EvictionRunEvent;
import org.neo4j.io.pagecache.tracing.MajorFlushEvent;
import org.neo4j.io.pagecache.tracing.PageCacheTracer;
import org.neo4j.jfr.configuration.Tracer;

/**
 * A special PageCacheMonitor that also produces Java Flight Recorder events.
 */
@Tracer("http://neo4j.com/io/pagecache/jfr/")
public class JfrPageCacheTracer implements PageCacheTracer
{
    static final Producer producer;
    static final EventToken faultToken;
    static final EventToken evictionToken;
    static final EventToken flushToken;
    static final EventToken pinToken;
    static final EventToken mappedFileToken;
    static final EventToken unmappedFileToken;
    static final EventToken evictionRunToken;
    static final EventToken fileFlushToken;
    static final EventToken cacheFlushToken;

    static
    {
        producer = createProducer();
        faultToken = createToken( JfrPageFaultEvent.class );
        evictionToken = createToken( JfrEvictionEvent.class );
        flushToken = createToken( JfrFlushEvent.class );
        pinToken = createToken( JfrPinEvent.class );
        mappedFileToken = createToken( JfrMappedFileEvent.class );
        unmappedFileToken = createToken( JfrUnmappedFileEvent.class );
        evictionRunToken = createToken( JfrEvictionRunEvent.class );
        fileFlushToken = createToken( JfrFileFlushEvent.class );
        cacheFlushToken = createToken( JfrCacheFlushEvent.class );
        producer.register();
    }

    private static Producer createProducer()
    {
        try
        {
            String producerUri = JfrPageCacheTracer.class.getAnnotation( Tracer.class ).value();
            return new Producer(
                    "PageCacheTracer",
                    "Tracing the runtime behaviour of the Neo4j PageCache",
                    new URI( producerUri ) );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    private static EventToken createToken( Class<? extends InstantEvent> type )
    {
        try
        {
            return producer.addEvent( type );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    private final AtomicLong evictionRunCounter = new AtomicLong();
    private final AtomicLong evictions = new AtomicLong();
    private final AtomicLong evictionExceptions = new AtomicLong();
    private final AtomicLong flushes = new AtomicLong();
    private final AtomicLong bytesWritten = new AtomicLong();
    private final AtomicLong bytesRead = new AtomicLong();
    private final AtomicLong pins = new AtomicLong();
    private final AtomicLong faults = new AtomicLong();
    private final AtomicLong unpins = new AtomicLong();
    private final AtomicLong hits = new AtomicLong();
    private final AtomicLong filesMapped = new AtomicLong();
    private final AtomicLong filesUnmapped = new AtomicLong();
    private final AtomicLong maxPages = new AtomicLong();
    private final EvictionEventStarter evictionEventStarter = new EvictionEventStarter(
            evictions, evictionExceptions, flushes, bytesWritten );
    private final PinEventStarter pinEventStarter =
            new PinEventStarter( pins, unpins, hits, faults, bytesRead, evictionEventStarter );

    @Override
    public void mappedFile( File file )
    {
        JfrMappedFileEvent event = new JfrMappedFileEvent( file.getAbsolutePath() );
        event.commit();
        filesMapped.getAndIncrement();
    }

    @Override
    public void unmappedFile( File file )
    {
        JfrUnmappedFileEvent event = new JfrUnmappedFileEvent( file.getAbsolutePath() );
        event.commit();
        filesUnmapped.getAndIncrement();
    }

    @Override
    public EvictionRunEvent beginPageEvictions( int expectedEvictions )
    {
        long evictionRunId = evictionRunCounter.incrementAndGet();

        JfrEvictionRunEvent event = new JfrEvictionRunEvent( evictionEventStarter );
        event.begin();
        event.setExpectedEvictions( expectedEvictions );
        event.setEvictionRun( evictionRunId );
        return event;
    }

    @Override
    public MajorFlushEvent beginFileFlush( PageSwapper swapper )
    {
        JfrFileFlushEvent event = new JfrFileFlushEvent( flushes, bytesWritten );
        event.begin();
        event.setFilename( swapper.file().getName() );
        return event;
    }

    @Override
    public MajorFlushEvent beginCacheFlush()
    {
        JfrCacheFlushEvent event = new JfrCacheFlushEvent( flushes, bytesWritten );
        event.begin();
        return event;
    }

    @Override
    public void pins( long pins )
    {
    }

    @Override
    public void unpins( long unpins )
    {
    }

    @Override
    public void hits( long hits )
    {
    }

    @Override
    public void faults( long faults )
    {
    }

    @Override
    public void bytesRead( long bytesRead )
    {
    }

    @Override
    public void evictions( long evictions )
    {
    }

    @Override
    public void evictionExceptions( long evictionExceptions )
    {
    }

    @Override
    public void bytesWritten( long bytesWritten )
    {
    }

    @Override
    public void flushes( long flushes )
    {
    }

    @Override
    public void maxPages( long maxPages )
    {
        this.maxPages.set( maxPages );
    }

    @Override
    public long faults()
    {
        return faults.get();
    }

    @Override
    public long evictions()
    {
        return evictions.get();
    }

    @Override
    public long pins()
    {
        return pins.get();
    }

    @Override
    public long unpins()
    {
        return unpins.get();
    }

    @Override
    public long hits()
    {
        return hits.get();
    }

    @Override
    public long flushes()
    {
        return flushes.get();
    }

    @Override
    public long bytesRead()
    {
        return bytesRead.get();
    }

    @Override
    public long bytesWritten()
    {
        return bytesWritten.get();
    }

    @Override
    public long filesMapped()
    {
        return filesMapped.get();
    }

    @Override
    public long filesUnmapped()
    {
        return filesUnmapped.get();
    }

    @Override
    public long evictionExceptions()
    {
        return evictionExceptions.get();
    }

    @Override
    public double hitRatio()
    {
        return MathUtil.portion( hits(), faults() );
    }

    @Override
    public double usageRatio()
    {
        return (faults.get() - evictions.get()) / (double) maxPages.get();
    }

    public PinEventStarter getPinEventStarter()
    {
        return pinEventStarter;
    }
}
