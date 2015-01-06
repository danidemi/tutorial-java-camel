package com.danidemi.tutorial.camel.support;

import static java.lang.String.format;

import org.apache.camel.Exchange;

/**
 * POJO that just prints out the received message.
 */
public class LogReceiver {
	
	private String name;
	
	public LogReceiver(String name) {
		super();
		this.name = name;
	}

	public LogReceiver() {
		this( new Object(){}.getClass().getEnclosingClass().getSimpleName() );
	}

	public void onExchange(Exchange e){
		System.out.println( format(" *** %s.onExchange() - %s *** ", name, e));
	}
	
}