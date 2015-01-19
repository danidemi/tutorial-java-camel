package com.danidemi.tutorial.camel.eip.systems;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;

import com.danidemi.tutorial.camel.support.CamelSampleSupport;
import com.danidemi.tutorial.camel.support.LogReceiver;
import com.danidemi.tutorial.camel.support.SampleDataSet;

public class PipesAndFilters extends CamelSampleSupport{
	
	@Override
	protected void populateRegistry(SimpleRegistry myRegistry) {
		super.populateRegistry(myRegistry);
		myRegistry.put("rx1", new LogReceiver("rx1"));
		myRegistry.put("rx2", new LogReceiver("rx2"));
		myRegistry.put("sampleDataSet", new SampleDataSet(3));
	}

	@Override
	protected RouteBuilder buildRoutes() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
							
				from("dataset:sampleDataSet").pipeline("bean:rx1?method=onExchange", "bean:rx2?method=onExchange");
				
			}
			
		};
	}
	
	public static void main(String[] args) {
		runSample(new PipesAndFilters());
	}

}
