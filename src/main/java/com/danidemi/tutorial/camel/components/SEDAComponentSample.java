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
 * {@link SedaComponent} asynchronous SEDA behavior, so that messages are exchanged on a BlockingQueue and consumers are invoked in a separate thread from the producer.
 * @see DirectComponentSample
 */
public class SEDAComponentSample extends CamelSampleSupport{
	
	@Override
	protected void populateRegistry(SimpleRegistry myRegistry) {
		myRegistry.put("sampleDataSet", new SampleDataSet(3));
	}

	@Override
	protected RouteBuilder buildRoutes() {

		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
							
				from("dataset:sampleDataSet?produceDelay=500")
					.to("bean:logRx?method=onExchange")
					.to("seda:d1");
				
				from("seda:d1")
					.delay(2000)
					.to("bean:logRx?method=onExchange");
				
			}
			
		};
	}
	
	public static void main(String[] args) {
		runSample(new SEDAComponentSample());
	}

}
