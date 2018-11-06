/**
 * Copyright (c) <2016> <hasder>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 	
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
*/
package com.eislab.af.translator;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class TranslationSystem {

	//XXX: This is how to write bugs
	
	// >> Entry point of system
	// 1) Start translator service thread
	// 3) Monitoring and reporting?
		
	/* Producer values */
	public static String EndpointPrefix = "/";
	public static int port = 8000;
	//XXX: public static Properties properties; What are properties used for? Method in translator_hub.java
	
	
	public static void main(String[] args)  {
		
//		loadProperties("translator.properties");
				
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath(EndpointPrefix);
		
		Server jettyServer = new Server(port);
        jettyServer.setHandler(context);
        
        ServletHolder jerseyServlet = context.addServlet( org.glassfish.jersey.servlet.ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
 
        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter( "jersey.config.server.provider.classnames", TranslatorService.class.getCanonicalName());
        			
		try {
//			ArrowheadAccessorRest accessor = new ArrowheadAccessorRest("http://127.0.0.1:8045");
//			//publish service
//			accessor.postPublishService("TranslationService", "_translator-s-ws-http._tcp", "127.0.0.1:8000", "/translator/" );
		
			//try create service interface
			try {
				jettyServer.start();
				jettyServer.join();
			} finally {
			    jettyServer.destroy();
			}
	 
			//unpublish service
//			accessor.postUnPublishService("TranslationService");
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		
	}

	// This function is not commented in file Translator_hub.java
	/** 
	 * Reads the properties from the file .properties
	 *
	 */
//	private static boolean loadProperties(String systemName) {
//	}

	
}
