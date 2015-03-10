package org.neo4j.jfr;

import io.airlift.airline.Cli;
import io.airlift.airline.help.Help;
import org.neo4j.jfr.commands.DumpConfiguration;

public class Main
{
    public static void main( String[] args )
    {
        Cli.CliBuilder<Runnable> builder = Cli.<Runnable>builder( "neo4j-jfr" )
                .withDescription( "Neo4j-JFR" )
                .withDefaultCommand( Help.class )
                .withCommand( Help.class ).withCommand( DumpConfiguration.class );

        builder.build().parse( args ).run();
    }
}
