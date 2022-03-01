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

import com.oracle.jrockit.jfr.EventDefinition;
import com.oracle.jrockit.jfr.TimedEvent;
import com.oracle.jrockit.jfr.ValueDefinition;

import java.util.concurrent.atomic.AtomicLong;

import org.neo4j.io.pagecache.tracing.PageFaultEvent;
import org.neo4j.io.pagecache.tracing.PinEvent;

@EventDefinition(path = "neo4j/io/pagecache/pin")
public class JfrPinEvent extends TimedEvent implements PinEvent
{
    static final String REL_KEY_PIN_EVENT_ID = "http://neo4j.com/jfr/pinId";

    private final AtomicLong unpins;
    private final AtomicLong hits;
    private final AtomicLong faults;
    private final AtomicLong bytesRead;
    private final EvictionEventStarter evictionEventStarter;

    @ValueDefinition(name = "pinEventId", relationKey = REL_KEY_PIN_EVENT_ID)
    private long pinEventId;
    @ValueDefinition(name = "cachePageId")
    private long cachePageId;
    @ValueDefinition(name = "exclusiveLock")
    private boolean exclusiveLock;
    @ValueDefinition(name = "filePageId")
    private long filePageId;
    @ValueDefinition(name = "filename")
    private String filename;
    @ValueDefinition(name = "causedPageFault")
    private boolean causedPageFault;

    public JfrPinEvent(
            AtomicLong unpins,
            AtomicLong faults,
            AtomicLong hits,
            AtomicLong bytesRead,
            EvictionEventStarter evictionEventStarter )
    {
        super( JfrPageCacheTracer.pinToken );
        this.unpins = unpins;
        this.hits = hits;
        this.faults = faults;
        this.bytesRead = bytesRead;
        this.evictionEventStarter = evictionEventStarter;
    }

    @Override
    public void setCachePageId( long cachePageId )
    {
        this.cachePageId = cachePageId;
    }

    @Override
    public PageFaultEvent beginPageFault()
    {
        faults.getAndIncrement();

        causedPageFault = true;
        JfrPageFaultEvent event = new JfrPageFaultEvent( bytesRead, evictionEventStarter );
        event.setPinEventId( pinEventId );
        event.setFilePageId( filePageId );
        event.setFilename( filename );
        event.begin();
        return event;
    }

    @Override
    public void hit()
    {
        hits.getAndIncrement();
    }

    public void setPinEventId( long pinEventId )
    {
        this.pinEventId = pinEventId;
    }

    public void setExclusiveLock( boolean exclusiveLock )
    {
        this.exclusiveLock = exclusiveLock;
    }

    public void setFilePageId( long filePageId )
    {
        this.filePageId = filePageId;
    }

    public void setFilename( String filename )
    {
        this.filename = filename;
    }

    public long getPinEventId()
    {
        return pinEventId;
    }

    public long getCachePageId()
    {
        return cachePageId;
    }

    public boolean getExclusiveLock()
    {
        return exclusiveLock;
    }

    public long getFilePageId()
    {
        return filePageId;
    }

    public String getFilename()
    {
        return filename;
    }

    public boolean getCausedPageFault()
    {
        return causedPageFault;
    }

    @Override
    public void done()
    {
        end();
        commit();
        unpins.getAndIncrement();
    }
}
