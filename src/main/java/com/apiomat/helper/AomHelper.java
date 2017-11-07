/* Copyright (c) 2011 - 2017 All Rights Reserved, http://www.apiomat.com/
 *
 * This source is property of apiomat.com. You are not allowed to use or distribute this code without a contract
 * explicitly giving you these permissions. Usage of this code includes but is not limited to running it on a server or
 * copying parts from it.
 *
 * Apinauten GmbH, Hainstrasse 4, 04109 Leipzig, Germany
 *
 * Oct 19, 2017
 * thum */
package com.apiomat.helper;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;

/**
 * Helper class with some static methods used in module tests
 *
 * @author thum
 */
public class AomHelper
{
	/**
	 * Returns the ID from the location header in the HTTP response
	 *
	 * @param response response from previous request
	 * @return the ID from the location header in the HTTP response
	 */
	public static String getIdFromLocationHeader( final HttpMethod response )
	{
		final String location = response.getResponseHeader( "Location" ).getValue( );
		return getIdFromUrl( location );
	}

	/**
	 * cuts the id off the url
	 *
	 * @param inputUrl the input url
	 *
	 * @return the id
	 */
	public static String getIdFromUrl( final String inputUrl )
	{
		if ( inputUrl == null )
		{
			return "";
		}
		int idx = inputUrl.lastIndexOf( '/' );
		if ( idx == -1 )
		{
			return "";
		}
		return inputUrl.substring( idx + 1 );
	}

	/**
	 * Reads a String from a InputStream without resetting the stream
	 *
	 * @param is the InputStream
	 * @return the string from id
	 */
	public static String getStringFromStream( InputStream is )
	{
		StringWriter writer = new StringWriter( );
		try
		{
			IOUtils.copy( is, writer, "UTF-8" );
		}
		catch ( IOException e )
		{
			return null;
		}
		return writer.toString( );
	}

	/**
	 * Exceutes an ant task of the modules build.xml programmatically
	 *
	 * @param target - ant-target
	 * @param path - system-path of nm
	 * @param userPassword - the customers password
	 * @return success-string
	 * @throws Exception exception
	 */
	public static String runAnttask( final String target, final String path, final String userPassword )
		throws Exception
	{
		String result = "";
		final Process process;
		try
		{
			/* set system in properties file and upload */
			final Properties sdkProp = new Properties( );
			sdkProp.load( new FileInputStream( new File( path + "/sdk.properties" ) ) );
			sdkProp.setProperty( "password", userPassword );
			sdkProp.store( new FileOutputStream( new File( path + "/sdk.properties" ) ), null );
			String antBin = "ant";
			if ( System.getenv( "ANT_BIN" ) != null )
			{
				antBin = System.getenv( "ANT_BIN" );
			}

			/* if running this test on a local windows-system, set the ANT_BIN env var. */
			final ProcessBuilder pb =
				new ProcessBuilder( antBin, "-Dant.build.javac.target=1.8", "-Dant.build.javac.source=1.8", "-f",
					"build.xml", target );
			if ( pb.environment( ).containsKey( "JAVA_HOME" ) == false )
			{
				/* For executing the testJava8 test, we sometimes need to set the java home to a java 8 JDK; if
				 * necessary, extend this on windows systems */
				pb.environment( ).put( "JAVA_HOME", "/opt/jdk" );
			}
			pb.directory( new File( path ) );
			pb.redirectErrorStream( true );
			process = pb.start( );

			final InputStream is = process.getInputStream( );
			final InputStreamReader isr = new InputStreamReader( is );
			final BufferedReader br = new BufferedReader( isr );
			String line;
			boolean successfull = false;
			boolean isNextResultLine = false;
			while ( ( line = br.readLine( ) ) != null )
			{
				System.out.println( line );
				if ( isNextResultLine )
				{ /* store the line after "BUILD FAILED" to get the correct error message */
					result = line;
					isNextResultLine = false;
				}
				if ( line.contains( "BUILD SUCCESSFUL" ) )
				{
					successfull = true;
					result = line;
				}
				else if ( line.contains( "BUILD FAILED" ) )
				{
					isNextResultLine = true;
				}

			}
			assertTrue( successfull );
			br.close( );

		}
		catch ( final Exception e )
		{
			e.printStackTrace( );
			Assert.fail( );
		}

		return result;
	}

	/**
	 * Unzips a byte array into a path
	 *
	 * @param data
	 *        data to unzip (assumes a ZipInputStream inside the byte array)
	 * @param targetPath the oath to unzip to
	 * @return number of files having been unzipped
	 */
	public static long unzip( final byte[ ] data, final String targetPath )
	{
		final InputStream input = new ByteArrayInputStream( data );
		final byte[ ] buffer = new byte[ 1024 ];
		final ZipInputStream zip = new ZipInputStream( input );
		long unzippedFiles = 0;
		try
		{
			int len = 0;
			ZipEntry e = zip.getNextEntry( );

			while ( e != null )
			{
				if ( !e.isDirectory( ) )
				{
					final ByteArrayOutputStream baos = new ByteArrayOutputStream( );
					while ( ( len = zip.read( buffer ) ) != -1 )
					{
						baos.write( buffer, 0, len );
					}
					baos.close( );

					final String filename = e.getName( );
					final byte[ ] fileData = baos.toByteArray( );
					FileUtils.writeByteArrayToFile( new File( targetPath + "/" + filename ), fileData );
					unzippedFiles++;
				}
				e = zip.getNextEntry( );
			}
			zip.close( );
		}
		catch ( IOException e )
		{
			Assert.fail( );
		}
		return unzippedFiles;
	}
}
