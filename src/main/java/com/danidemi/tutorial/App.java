package com.danidemi.tutorial;

import org.apache.camel.CamelContext;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.RouteBuilder;
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
			from("direct:a").to("direct:b");			
		}
		
	}

	private static void dodo() throws Exception {
		// CamelContext, a container for Components, Routes etc...
		CamelContext context = new DefaultCamelContext();
				
		context.start();
		context.stop();
		
//		RoutesBuilder builder = RouteBuilder.
//		context.addRoutes( new MyAppRouteBuilder());
	}
	
}
