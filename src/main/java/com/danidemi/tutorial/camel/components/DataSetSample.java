package com.danidemi.tutorial.camel.components;

import org.apache.camel.builder.RouteBuilder;

import com.danidemi.tutorial.camel.support.CamelSampleSupport;

public class DataSetSample extends CamelSampleSupport{

	@Override
	protected RouteBuilder buildRoutes() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
							
				from("dataset:sampleDataSet").to("bean:logRx?method=onExchange");
				
			}
			
		};
	}
	
	public static void main(String[] args) {
		runSample(new DataSetSample());
	}

}
