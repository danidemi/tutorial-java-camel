package com.danidemi.tutorial;

import java.util.Hashtable;

import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

import net.timewalker.ffmq3.FFMQConstants;

import com.danidemi.jlubricant.embeddable.ServerException;
import com.danidemi.jlubricant.embeddable.ffmq.core.FileBasedFFMQEmbeddableServer;
import com.danidemi.jlubricant.embeddable.ffmq.sample.MyAsyncReader;
import com.danidemi.jlubricant.embeddable.ffmq.sample.MyProducer;
import com.danidemi.jlubricant.embeddable.ffmq.sample.MySyncReader;

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
		
		FileBasedFFMQEmbeddableServer server = new FileBasedFFMQEmbeddableServer();
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
		
		new MyProducer("pr1",  context, 2, "TEST-QUEUE").start();
		new MyProducer("pr2",  context, 1, "TEST-QUEUE").start();
		new MySyncReader("s-rx-1", context, "TEST-QUEUE").start();
		new MySyncReader("s-rx-2", context, "TEST-QUEUE").start();
		new MySyncReader("s-rx-3", context, "TEST-QUEUE").start();
		new MyAsyncReader("a-rx-1", context, "TEST-QUEUE").start();
		
		
		
			

		
		
		Thread.sleep(20000);
		

		
		server.stop();
		
	}
	
}
