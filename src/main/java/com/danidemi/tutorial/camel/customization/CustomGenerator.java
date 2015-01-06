package com.danidemi.tutorial.camel.customization;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomGenerator extends DefaultComponent {
	
	private static final Logger log = LoggerFactory.getLogger(CustomGenerator.class);

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
	
		log.info("Creating endpoint\n{}\n{}\n{}", uri, remaining, parameters);
		
		Endpoint endpoint = new CustomGeneratorEndpoint(uri);
		return endpoint;
	}
	
	class CustomGeneratorEndpoint extends DefaultEndpoint {

		private String uri;
		private Producer producer;

		public CustomGeneratorEndpoint(String uri) {
			this.uri = uri;
		}

		public Producer createProducer() throws Exception {
			return new CustomGeneratorProducer(this);
		}

		public Consumer createConsumer(Processor processor) throws Exception {
			return new CustomGeneratorConsumer(this, processor);
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
			log.info("Starting Endpoint {}", this.getClass().getName());
			producer = createProducer();
			producer.start();
		}
		
		@Override
		public void stop() throws Exception {
			log.info("Stopping Endpoint {}", this.getClass().getName());
			producer.stop();
		}
		
	}
	
	class CustomGeneratorProducer extends DefaultProducer {

		public CustomGeneratorProducer(Endpoint endpoint) {
			super(endpoint);
			// TODO Auto-generated constructor stub
		}

		public void process(Exchange exchange) throws Exception {
			log.info("Processing {}", exchange);
		}
		
		@Override
		public void start() throws Exception {
			log.info("Starting Producer {}", this.getClass().getName());
			
			Exchange newExchange = getEndpoint().createExchange(ExchangePattern.InOut);
			DefaultMessage msg = new DefaultMessage();
			msg.setBody("This is created by custom");
			newExchange.setIn( msg );
			
			
		}
		
		@Override
		public void stop() throws Exception {
			log.info("Stopping Producer {}", this.getClass().getName());
		}
		
	}
	
	class CustomGeneratorConsumer extends DefaultConsumer {

		public CustomGeneratorConsumer(Endpoint endpoint, Processor processor) {
			super(endpoint, processor);
			// TODO Auto-generated constructor stub
		}
		
		
		
		@Override
		public void start() throws Exception {
			log.info("Starting Consumer {}", this.getClass().getName());
			final Processor theProcessor = getProcessor();
			
			Runnable runnable = new Runnable(){

				public void run() {
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
