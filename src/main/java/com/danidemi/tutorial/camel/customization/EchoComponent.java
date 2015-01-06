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
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.impl.DefaultProducer;
import org.apache.camel.impl.ProducerCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoComponent extends DefaultComponent {
	
	private static final Logger log = LoggerFactory.getLogger(EchoComponent.class);

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
	
		if(!uri.startsWith("echo://")) return null;
		
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

		public Producer createProducer() throws Exception {
			log.info("Creating producer for {}.", uriName);
			return new EchoProducer(this);
		}

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
				log.info("Echoing incoming message.", exchange);
				
				Message in = exchange.getIn();
				DefaultMessage dm = new DefaultMessage();
				dm.setBody( format( "Echo of '%s'", in ) );
				exchange.setOut( dm );	
				
				Endpoint fromEndpoint = exchange.getFromEndpoint();
				fromEndpoint.createProducer().process(exchange);
				
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
