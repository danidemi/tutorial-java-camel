package com.danidemi.tutorial.camel.support;

import static java.lang.String.format;

import org.apache.camel.Exchange;

/**
 * POJO that just prints out the received message.
 */
public class LogReceiver {
	
	public void onExchange(Exchange e){
		System.out.println( format(" *** onExchange() - %s *** ", e));
	}
	
}