package com.danidemi.tutorial.camel.customization;

import org.apache.camel.ExchangePattern;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;

import com.danidemi.tutorial.camel.support.CamelSampleSupport;
import com.danidemi.tutorial.camel.support.LogReceiver;

public class EchoComponentSample extends CamelSampleSupport{
	
	@Override
	protected void populateRegistry(SimpleRegistry myRegistry) {
		super.populateRegistry(myRegistry);
	}

	@Override
	protected RouteBuilder buildRoutes() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
							
				from("echo:e1").setExchangePattern(ExchangePattern.InOut).process(new InvertProcessor()).to("echo:e2");
				
			}
			
		};
	}
	
	public static void main(String[] args) {
		runSample(new EchoComponentSample());
	}

}
