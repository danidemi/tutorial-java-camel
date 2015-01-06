package com.danidemi.tutorial.camel.applications;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.NamingException;

import net.timewalker.ffmq3.FFMQServer;
import net.timewalker.ffmq3.jndi.FFMQConnectionFactory;
import net.timewalker.ffmq3.management.destination.definition.QueueDefinition;
import net.timewalker.ffmq3.management.destination.definition.TopicDefinition;
import net.timewalker.ffmq3.utils.Settings;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.processor.interceptor.Delayer;
import org.apache.camel.util.jndi.JndiContext;
import org.apache.commons.io.FileUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ffmq.core.FFMQEmbeddableServer;
import com.danidemi.jlubricant.embeddable.ffmq.core.SingleFolderConfiguration;
import com.danidemi.jlubricant.embeddable.ffmq.core.TcpListenerDescriptor;
import com.danidemi.tutorial.camel.support.CamelSampleSupport;

public class JmsApp extends CamelSampleSupport {
	
	private FFMQEmbeddableServer emb;

	@Override
	protected void startServices() throws ServerException {
		
		QueueDefinition queue1 = new QueueDefinition();
		queue1.setMaxBlockCount(1024);
		queue1.setName("the-queue");
		queue1.setAutoExtendAmount(1024);
		queue1.setInitialBlockCount(1024);
		List<QueueDefinition> mqueueDefinitions = Arrays.asList(queue1);
		
		Settings mengineSettings = new SingleFolderConfiguration(new File(
				FileUtils.getTempDirectory(), "ffmq")).asSettings();

		TcpListenerDescriptor mtcpListener = new TcpListenerDescriptor(
				"0.0.0.0", 10002, new Settings());
		
		emb = new FFMQEmbeddableServer("engine",
				mtcpListener, mqueueDefinitions, new ArrayList<TopicDefinition>(),
				mengineSettings);
		
		emb.start();
	}
	
	@Override
	protected void stopServices() throws ServerException {
		emb.stop();
	}
	
	@Override
	protected void populateRegistry(SimpleRegistry myRegistry) {
		FFMQConnectionFactory jmxConnectionFactory = new FFMQConnectionFactory();
		myRegistry.put("jmxConnectionFactory", jmxConnectionFactory);
	}

	@Override
	protected RouteBuilder buildRoutes() {
		return new RouteBuilder() {

			@Override
			public void configure() throws Exception {
							
				from("jms:queue:the-queue?connectionFactory=jmxConnectionFactory")
				.delay(1000)
				.process(new Processor() {
					
					public void process(Exchange exchange) throws Exception {
						org.apache.camel.Message in = exchange.getIn();
						Object body = in.getBody();
						System.out.println(body);
					}
				})
				.to("bean:logRx?method=onExchange");
				
			}
			
		};
	}
	
	@Override
	protected void fireRequests() throws NamingException, JMSException {
		Context context = emb.jndiCtx();
		QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("factory/QueueConnectionFactory");
		JmsTemplate jmsTemplate = new JmsTemplate( connectionFactory );
		jmsTemplate.send("the-queue", new MessageCreator() {
			
			public Message createMessage(Session sess) throws JMSException {
				TextMessage createTextMessage = sess.createTextMessage();
				createTextMessage.setText("Message on the queue");
				return createTextMessage;
			}
		});
	}
	
	public static void main(String[] args) {
		runSample(new JmsApp());
	}

}
