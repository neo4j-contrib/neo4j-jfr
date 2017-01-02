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
package org.neo4j.kernel.impl.transaction.tracing.jfr;

import com.oracle.jrockit.jfr.EventDefinition;
import com.oracle.jrockit.jfr.TimedEvent;
import com.oracle.jrockit.jfr.ValueDefinition;

import org.neo4j.kernel.impl.transaction.tracing.CommitEvent;
import org.neo4j.kernel.impl.transaction.tracing.LogAppendEvent;
import org.neo4j.kernel.impl.transaction.tracing.StoreApplyEvent;

@EventDefinition(path = "neo4j/transaction/commit")
public class JfrCommitEvent extends TimedEvent implements CommitEvent
{
    @ValueDefinition(name = "transactionId")
    private long transactionId;

    protected JfrCommitEvent()
    {
        super( JfrTransactionTracer.commitToken );
    }

    @Override
    public void close()
    {
        commit();
    }

    @Override
    public LogAppendEvent beginLogAppend()
    {
        JfrLogAppendEvent event = new JfrLogAppendEvent();
        event.begin();
        return event;
    }

    @Override
    public StoreApplyEvent beginStoreApply()
    {
        JfrStoreApplyEvent event = new JfrStoreApplyEvent();
        event.begin();
        return event;
    }

    @Override
    public void setTransactionId( long transactionId )
    {
        this.transactionId = transactionId;
    }

    public long getTransactionId()
    {
        return transactionId;
    }
}
