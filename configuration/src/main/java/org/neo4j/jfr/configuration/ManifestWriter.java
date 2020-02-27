/**
 * Copyright (c) 2002-2020 "Neo Technology,"
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

import com.oracle.jrockit.jfr.EventDefinition;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import static java.lang.String.format;

@SupportedAnnotationTypes({"org.neo4j.jfr.configuration.Tracer", "com.oracle.jrockit.jfr.EventDefinition"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ManifestWriter extends AbstractProcessor
{
    private static final String PATH_KEY = "MANIFEST_PATH";
    private static final String MANIFEST_FILE = "manifest.txt";

    @Override
    public boolean process( Set<? extends TypeElement> annotations, RoundEnvironment roundEnv )
    {
        if ( roundEnv.processingOver() )
        {
            return true;
        }

        Map<Element, String> packageToUri = new HashMap<>();

        for ( Element theClass : roundEnv.getElementsAnnotatedWith( Tracer.class ) )
        {
            Element thePackage = theClass.getEnclosingElement();
            String uri = theClass.getAnnotation( Tracer.class ).value();
            packageToUri.put( thePackage, uri );
        }

        Map<Element, Set<String>> packageToPath = new HashMap<>();

        for ( Element theClass : roundEnv.getElementsAnnotatedWith( EventDefinition.class ) )
        {
            Element thePackage = theClass.getEnclosingElement();
            String path = theClass.getAnnotation( EventDefinition.class ).path();
            Set<String> thePaths = packageToPath.get( thePackage );
            if ( thePaths == null )
            {
                thePaths = new HashSet<>();
                packageToPath.put( thePackage, thePaths );
            }
            thePaths.add( path );
        }

        String pathAsString = System.getProperty( PATH_KEY, "target/classes" );
        File path = new File( pathAsString );

        try
        {
            Files.createDirectories( path.toPath() );

            File manifestFile = new File( path, MANIFEST_FILE );

            try ( FileOutputStream fileOutputStream = new FileOutputStream( manifestFile ) )
            {
                for ( Element element : packageToUri.keySet() )
                {
                    String uri = packageToUri.get( element );
                    Set<String> paths = packageToPath.get( element );
                    fileOutputStream.write( uri.getBytes( "UTF-8" ) );
                    fileOutputStream.write( ',' );
                    fileOutputStream.write( StringUtils.join( paths, ',' ).getBytes( "UTF-8" ) );
                    fileOutputStream.write( '\n' );
                }
            }
        }
        catch ( IOException e )
        {
            throw new IllegalStateException( format( "Unable to create manifest [%s=%s]", PATH_KEY, path ), e );
        }

        return true;
    }

    public void write()
    {
        InputStream inputStream = getClass().getResourceAsStream( "/manifest.txt" );

        try
        {
            char[] cbuf = new char[1000];
            new InputStreamReader( inputStream ).read( cbuf );
            System.out.println( Arrays.toString( cbuf ) );
        }
        catch ( IOException e )
        {
            throw new UnsupportedOperationException( "TODO", e );
        }
        throw new UnsupportedOperationException( "TODO" );
    }
}
