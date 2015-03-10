/**
 * Copyright (c) 2002-2015 "Neo Technology,"
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

import com.oracle.jrockit.jfr.EventToken;
import com.oracle.jrockit.jfr.InstantEvent;
import com.oracle.jrockit.jfr.Producer;

import java.net.URI;

import org.neo4j.jfr.configuration.Tracer;
import org.neo4j.kernel.impl.transaction.tracing.TransactionEvent;
import org.neo4j.kernel.impl.transaction.tracing.TransactionTracer;

@Tracer("http://neo4j.com/kernel/transaction/jfr")
public class JfrTransactionTracer implements TransactionTracer
{
    //static final String producerUri = "http://neo4j.com/kernel/transaction/jfr";
    static final Producer producer;
    static final EventToken commitToken;
    static final EventToken logAppendToken;
    static final EventToken logForceToken;
    static final EventToken logForceWaitToken;
    static final EventToken logRotateToken;
    static final EventToken serializeTransactionToken;
    static final EventToken transactionToken;
    static final EventToken storeApplyToken;

    static
    {
        producer = createProducer();
        commitToken = createToken( JfrCommitEvent.class );
        logAppendToken = createToken( JfrLogAppendEvent.class );
        logForceToken = createToken( JfrLogForceEvent.class );
        logForceWaitToken = createToken( JfrLogForceWaitEvent.class );
        logRotateToken = createToken( JfrLogRotateEvent.class );
        serializeTransactionToken = createToken( JfrSerializeTransactionEvent.class );
        transactionToken = createToken( JfrTransactionEvent.class );
        storeApplyToken = createToken( JfrStoreApplyEvent.class );
        producer.register();
    }

    private static Producer createProducer()
    {
        try
        {
            String producerUri = JfrTransactionTracer.class.getAnnotation( Tracer.class ).value();
            return new Producer(
                    "TransactionTracer",
                    "Tracing the runtime behaviour of the Neo4j transaction subsystem",
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

    @Override
    public TransactionEvent beginTransaction()
    {
        return new JfrTransactionEvent();
    }
}
