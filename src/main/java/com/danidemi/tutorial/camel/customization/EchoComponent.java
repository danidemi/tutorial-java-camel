package com.danidemi.tutorial.camel.customization;

import static java.lang.String.format;

import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.impl.ProducerCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link EchoComponent} just sends back the same input received.
 * It supports the <code>echo://&lt;component_name&gt;</code> URI.
 */
public class EchoComponent extends DefaultComponent {
	
	private static final Logger log = LoggerFactory.getLogger(EchoComponent.class);

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
	
		// if the provided uri is not understood, let's return null, meaning "don't know how to interpret it. 
		if(!uri.startsWith("echo://")) return null;
		
		// let's retrieve the endpoint name.
		String uriName = uri.substring( "echo://".length() );
	
		log.info(
				"Creating endpoint"
				+ "\n\turi={}"
				+ "\n\tremaining={}"
				+ "\n\tparameters={}" 
				+ "\n\turiname={}", uri, remaining, parameters, uriName);
		
		Endpoint endpoint = new EchoEndpoint(uri, uriName);
		return endpoint;
	}
	
	class EchoEndpoint extends DefaultEndpoint {

		private String uri;
		private String uriName;

		public EchoEndpoint(String uri, String uriName) {
			this.uri = uri;
			this.uriName = uriName;
		}

		/**
		 * The {@link Producer} is the piece that produces pieces of info for the outside world.
		 * Since this component just echoes the incoming Request/Reply, no {@link Producer} is needed.
		 */
		public Producer createProducer() throws Exception {
			
			log.info("Creating producer for {}.", uriName);
			return new EchoProducer(this);
			//return null;
		}

		/**
		 * The {@link Consumer} is the piece that reads info from the outside world and puts data into Camel.
		 */		
		public Consumer createConsumer(Processor processor) throws Exception {
			log.info("Creating consumer for {}.", uriName);
			return new EchoConsumer(this, processor);
		}

		public boolean isSingleton() {
			return true;
		}
		
		@Override
		protected String createEndpointUri() {
			return uri;
		}
		
		@Override
		public void start() throws Exception {
			log.info("Starting Endpoint {}", this);
		}
		
		@Override
		public void stop() throws Exception {
			log.info("Stopping Endpoint {}", this);
		}
		
		@Override
		public String toString() {
			return uriName;
		}
		
	}
	
	class EchoProducer extends DefaultProducer {
		
		public EchoProducer(EchoEndpoint endpoint) {
			super(endpoint);
		}

		public void process(Exchange exchange) throws Exception {
			log.info("Received {} in Producer of {}", exchange, getEndpoint());
			
			if(!exchange.hasOut()){
				
				Message response = exchange.getIn().copy();
				response.setBody( "Echo of '" + response.getBody() + "'" );
				exchange.setOut( response );
				
			}else{
				System.out.println( format(
						"***********************************\n"
						+ "%s"
						+ "\n***********************************", exchange.getOut().getBody()));
			}
		
		}
		
		@Override
		public void start() throws Exception {
			log.info("Starting Producer {} for {}", this, getEndpoint());			
		}
		
		@Override
		public void stop() throws Exception {
			log.info("Stopping Producer {} for {}", this, getEndpoint());
		}
		
	}
	
	class EchoConsumer extends DefaultConsumer {

		public EchoConsumer(EchoEndpoint endpoint, Processor processor) {
			super(endpoint, processor);
		}
		
		@Override
		public void start() throws Exception {
			log.info("Starting Consumer {} for Endpoint {}", this.getClass().getName(), getEndpoint());
			final Processor theProcessor = getProcessor();
			
			Runnable runnable = new Runnable(){

				public void run() {
					log.info("Creating new Exchange for Endpoint {}", getEndpoint());
					Exchange createExchange = getEndpoint().createExchange(ExchangePattern.InOut);
					DefaultMessage in = new DefaultMessage();
					in.setBody("This has been generated");
					createExchange.setIn(in);
					try {
						theProcessor.process(createExchange);
						log.info("Reply is: {}", createExchange);
					} catch (Exception e) {
						log.error("An error occurred", e);
					}
				}
				
			};
			
			new Thread(runnable).start();
		}
		
		@Override
		public void stop() throws Exception {
			log.info("Stopping Consumer {}", this.getClass().getName());
		}
		
	}	

}
