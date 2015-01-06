package com.danidemi.tutorial.camel.support;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

public abstract class CamelSampleSupport {

	protected abstract RouteBuilder buildRoutes();

	protected void run() throws Exception {
				
		DefaultCamelContext context = buildContext();
				
		context.addRoutes( buildRoutes() );
				
		context.start();
		Thread.sleep(8000);
		context.stop();
	}
	
	/** 
	 * CamelContext, a container for Components, Routes etc...
	 */
	DefaultCamelContext buildContext() {
		DefaultCamelContext context = new DefaultCamelContext();
		context.setRegistry( buildRegistry() );
		return context;
	}

	private Registry buildRegistry() {
		SimpleRegistry myRegistry = new SimpleRegistry();
		myRegistry.put("logRx", new LogReceiver());
		myRegistry.put("sampleDataSet", new SampleDataSet());
		return myRegistry;
	}	
	
	protected static void runSample(CamelSampleSupport dataSetSample) {
		try {
			Class<?> enclosingClass = new Object() { }.getClass().getEnclosingClass();
			System.out.println(enclosingClass);
			dataSetSample.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
