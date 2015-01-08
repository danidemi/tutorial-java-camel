package com.danidemi.tutorial.camel.components;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.dataset.DataSet;
import org.apache.camel.component.dataset.DataSetSupport;
import org.apache.camel.component.direct.DirectComponent;
import org.apache.camel.component.seda.SedaComponent;
import org.apache.camel.impl.SimpleRegistry;

import com.danidemi.tutorial.camel.support.CamelSampleSupport;
import com.danidemi.tutorial.camel.support.SampleDataSet;

/**
 * {@link DirectComponent} provides direct, synchronous invocation of any consumers when a producer sends a message exchange.
 * @see SEDAComponentSample 
 */
public class DirectComponentSample extends CamelSampleSupport{
	
	@Override
	protected void populateRegistry(SimpleRegistry myRegistry) {
		myRegistry.put("sampleDataSet", new SampleDataSet(3));
	}

	@Override
	protected RouteBuilder buildRoutes() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
							
				from("dataset:sampleDataSet?produceDelay=1000")
					.to("bean:logRx?method=onExchange")
					.to("direct:d1");
				
				from("direct:d1")
					.delay(2000)
					.to("bean:logRx?method=onExchange");
				
			}
			
		};
	}
	
	public static void main(String[] args) {
		runSample(new DirectComponentSample());
	}

}
