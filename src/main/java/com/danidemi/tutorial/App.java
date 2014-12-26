package com.danidemi.tutorial;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.impl.DefaultCamelContext;

public class App {
	
	public static void main(String[] args) {
		
		try {
			dodo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class MyAppRouteBuilder extends RouteBuilder {

		@Override
		public void configure() throws Exception {
					
			from("file://source").to("file://destination");			
		}
		
	}

	private static void dodo() throws Exception {
		
		// CamelContext, a container for Components, Routes etc...
		CamelContext context = new DefaultCamelContext();
				
		context.addRoutes( new MyAppRouteBuilder() );
				
		context.start();
		
		ProducerTemplate template = context.createProducerTemplate();	
		template.sendBody("file://destination", "A message");
		
		context.stop();
		
	}
	
}
