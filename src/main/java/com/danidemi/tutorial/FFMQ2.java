package com.danidemi.tutorial;

import static java.lang.String.format;

import java.util.Hashtable;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.timewalker.ffmq3.FFMQConstants;
import net.timewalker.ffmq3.local.connection.LocalConnection;

import com.danidemi.jlubricant.embeddable.ServerException;

public class FFMQ2 {

	public static void main(String[] args) {
		
		try {
			dodo();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void dodo() throws ServerException, NamingException, JMSException, InterruptedException {
		
		FFMQEmbeddableServer server = new FFMQEmbeddableServer();
		server.start();
		
		
		
		
		
		// Create and initialize a JNDI context
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, FFMQConstants.JNDI_CONTEXT_FACTORY);
		env.put(Context.PROVIDER_URL, "tcp://localhost:10002");
		Context context = new InitialContext(env);
		
		NamingEnumeration<NameClassPair> list = context.list(context.getNameInNamespace());
		while(list.hasMore()){
			NameClassPair nextElement = list.nextElement();
			System.out.println(nextElement.getName() + " = " + nextElement.getClassName());
		}
		
		new MyProducer("pr1",  context, 2).start();
		new MyProducer("pr2",  context, 1).start();
		new MySyncReader("s-rx-1", context).start();
		new MySyncReader("s-rx-2", context).start();
		new MySyncReader("s-rx-3", context).start();
		new MyAsyncReader("a-rx-1", context).start();
		
		
		
			

		
		
		Thread.sleep(20000);
		

		
		server.stop();
		
	}
	
	private static class MyAsyncReader implements MessageListener {
		private Context context;
		private String name;
		public MyAsyncReader(String name, Context context) {
			super();
			this.context = context;
			this.name = name;
		}		
		public void start(){
			new Thread(new Runnable() {
				
				public void run() {
					try {
						QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("factory/QueueConnectionFactory");
						QueueConnection queueConnection = connectionFactory.createQueueConnection();
						QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
						Queue queue = queueSession.createQueue("TEST-QUEUE");
						QueueReceiver queueReceiver = queueSession.createReceiver(queue);
						queueReceiver.setMessageListener(MyAsyncReader.this);
						
						queueConnection.start();
						
						System.out.println("thred terminated");
												
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		
		public void onMessage(Message message) {
			try {
				String msg = (String) new VisitableMessage(message).accept( new RejectAllMessageVisitor<String>(){
					public String onTextMessage(TextMessage message) throws JMSException { return message.getText(); };
				} );
				System.out.println(format("Receiver %s received message \"%s\"", name, msg));	
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}		
	}	
	
	private static class MySyncReader {
		private Context context;
		private String name;
		public MySyncReader(String name, Context context) {
			super();
			this.context = context;
			this.name = name;
		}		
		public void start(){
			new Thread(new Runnable() {
				
				public void run() {
					try {
						QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("factory/QueueConnectionFactory");
						QueueConnection queueConnection = connectionFactory.createQueueConnection();
						QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
						Queue queue = queueSession.createQueue("TEST-QUEUE");
						QueueReceiver queueReceiver = queueSession.createReceiver(queue);
						
						queueConnection.start();
						
						boolean goOn = true;
						while(goOn) {
							Message receive = null;
							try{
								receive = queueReceiver.receive();
								
								String msg = (String) new VisitableMessage(receive).accept( new RejectAllMessageVisitor<String>(){
									public String onTextMessage(TextMessage message) throws JMSException { return message.getText(); };
								} );
								
								System.out.println(format("Receiver %s received message \"%s\"", name, msg));								
							}catch(Exception e){
								goOn = false;
							}
							if(receive == null){
								goOn = false; // the consumer closed
							}
						}
						queueReceiver.close();
						
						if(queueSession.getTransacted()){
							queueSession.commit();			
						}
						
						queueConnection.close();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}		
	}
	
	private static class MyProducer {
		
		private Context context;
		private int numOfMsgs = 3;
		private String name;

		public MyProducer(String name, Context context, int num) {
			this.context = context;
			this.name = name;
			this.numOfMsgs = num;
		}

		public void start(){
			new Thread(new Runnable() {
				
				public void run() {
					try {
						QueueConnectionFactory connectionFactory = (QueueConnectionFactory)context.lookup("factory/QueueConnectionFactory");
						QueueConnection queueConnection = connectionFactory.createQueueConnection();
						QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
						Queue queue = queueSession.createQueue("TEST-QUEUE");
						
						MessageProducer producer = queueSession.createProducer(queue);
						for(int i=0; i<numOfMsgs; i++){
							TextMessage textMessage = queueSession.createTextMessage();
							String body = format("msg #%s/%s", name, i);
							textMessage.setText(body);
							producer.send( textMessage );
							System.out.println( format("sent \"%s\"", body) );
							Thread.yield();
							Thread.sleep(1000);
						}
						producer.close();
						
						if(queueSession.getTransacted()){
							queueSession.commit();			
						}
						
						queueConnection.close();
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}
		
	}
	
}
