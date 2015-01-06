package com.danidemi.tutorial.camel.support;

import static java.lang.String.format;

import javax.naming.NamingException;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.spi.Registry;

public abstract class CamelSampleSupport {

	protected abstract RouteBuilder buildRoutes();

	protected void run() throws Exception {
				
		startServices();
		
		DefaultCamelContext context = buildContext();
		context.addRoutes( buildRoutes() );
		context.start();
		
		fireRequests();
		
		Thread.sleep(8000);
		context.stop();
		
		stopServices();
	}
	
	protected void fireRequests() throws Exception {
	}

	protected void stopServices() throws Exception {
	}

	protected void startServices() throws Exception {
	}

	/** 
	 * CamelContext, a container for Components, Routes etc...
	 */
	protected DefaultCamelContext buildContext() {
		DefaultCamelContext context = new DefaultCamelContext();
		context.setRegistry( buildRegistry() );
		return context;
	}

	protected Registry buildRegistry() {
		SimpleRegistry myRegistry = new SimpleRegistry();
		myRegistry.put("logRx", new LogReceiver());
		myRegistry.put("sampleDataSet", new SampleDataSet());
		populateRegistry(myRegistry);
		return myRegistry;
	}

	protected void populateRegistry(SimpleRegistry myRegistry) {
	}	
	
	protected static void runSample(CamelSampleSupport dataSetSample) {
		System.out.println(
				format("*******************************\nRunning %s\n*******************************\n", dataSetSample.getClass().getSimpleName())
		);
		try {
			Class<?> enclosingClass = new Object() { }.getClass().getEnclosingClass();
			System.out.println(enclosingClass);
			dataSetSample.run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
