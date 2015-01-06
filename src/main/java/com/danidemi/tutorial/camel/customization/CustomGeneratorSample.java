package com.danidemi.tutorial.camel.customization;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;

import com.danidemi.tutorial.camel.support.CamelSampleSupport;
import com.danidemi.tutorial.camel.support.LogReceiver;

public class CustomGeneratorSample extends CamelSampleSupport{
	
	@Override
	protected void populateRegistry(SimpleRegistry myRegistry) {
		super.populateRegistry(myRegistry);
		myRegistry.put("rx1", new LogReceiver("rx1"));
		myRegistry.put("rx2", new LogReceiver("rx2"));
	}

	@Override
	protected RouteBuilder buildRoutes() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
							
				//from("dataset:sampleDataSet").pipeline("bean:rx1?method=onExchange", "bean:rx2?method=onExchange");
				//from("dataset:sampleDataSet").pipeline("bean:rx1?method=onExchange", "customgenerator:g1");
				from("customgenerator:g1").to("bean:logRx");
				
			}
			
		};
	}
	
	public static void main(String[] args) {
		runSample(new CustomGeneratorSample());
	}

}
