package com.danidemi.tutorial;

import static java.lang.String.format;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

public class Camel01 {

	private void run() throws Exception {
		
		SimpleRegistry myRegistry = new SimpleRegistry();
		myRegistry.put("logRx", new LogReceiver());
		
		// CamelContext, a container for Components, Routes etc...
		DefaultCamelContext context = new DefaultCamelContext();
		context.setRegistry( myRegistry );
				
		context.addRoutes( new Camel01Config() );
				
		context.start();
		
		Thread.sleep(8000);
		
		context.stop();
	}
	
	public static void main(String[] args) {
		try {
			new Camel01().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class LogReceiver {
		
		public void onMessage(Exchange e){
			System.out.println( format(" *** A message arrived: %s *** ", e));
		}
		
	}
	
	static class Camel01Config extends RouteBuilder {

		@Override
		public void configure() throws Exception {
						
			from("timer:test-timer?period=2000&repeatCount=4").to("bean:logRx?method=onMessage");
			
		}
		
	}
	
}
