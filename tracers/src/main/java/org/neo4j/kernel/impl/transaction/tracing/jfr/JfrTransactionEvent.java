/**
 * Copyright (c) 2002-2018 "Neo Technology,"
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
import org.neo4j.kernel.impl.transaction.tracing.TransactionEvent;

@EventDefinition(path = "neo4j/transaction/transaction")
public class JfrTransactionEvent extends TimedEvent implements TransactionEvent
{
    @ValueDefinition(name = "success")
    private boolean success;
    @ValueDefinition(name = "failure")
    private boolean failure;
    @ValueDefinition(name = "transactionType")
    private String transactionType;
    @ValueDefinition(name = "readOnly")
    private boolean readOnly;

    @Override
    public void setSuccess( boolean success )
    {
        this.success = success;
    }

    @Override
    public void setFailure( boolean failure )
    {
        this.failure = failure;
    }

    @Override
    public CommitEvent beginCommitEvent()
    {
        JfrCommitEvent event = new JfrCommitEvent();
        event.begin();
        return event;
    }

    @Override
    public void close()
    {
        commit();
    }

    @Override
    public void setTransactionType( String transactionType )
    {
        this.transactionType = transactionType;
    }

    @Override
    public void setReadOnly( boolean readOnly )
    {
        this.readOnly = readOnly;
    }

    public boolean getSuccess()
    {
        return success;
    }

    public boolean getFailure()
    {
        return failure;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public boolean getReadOnly()
    {
        return readOnly;
    }
}
