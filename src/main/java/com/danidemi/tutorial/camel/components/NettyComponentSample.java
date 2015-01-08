package com.danidemi.tutorial.camel.components;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.SimpleRegistry;

import com.danidemi.tutorial.camel.support.CamelSampleSupport;
import com.danidemi.tutorial.camel.support.SampleDataSet;

/**
 * Send data to a netty socket.
 */
public class NettyComponentSample extends CamelSampleSupport {

	@Override
	protected void populateRegistry(SimpleRegistry myRegistry) {
		myRegistry.put("sampleDataSet", new SampleDataSet(3));
	}

	@Override
	protected RouteBuilder buildRoutes() {
		RouteBuilder routeBuilder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				
				from("netty:tcp://localhost:5155?sync=true&textline=true")
				.to("echo://myecho");				
			}

		};
		
		return routeBuilder;
	}

	@Override
	protected void fireRequests() throws Exception {
		new Thread(new Runnable() {

			public void run() {
				try {
					
					String messageSent = "Hello!";
					
					Thread.sleep(1000);
										
				    Socket echoSocket = new Socket("localhost", 5155);
				    PrintWriter out =
				        new PrintWriter(echoSocket.getOutputStream(), true);
				    BufferedReader in =
				        new BufferedReader(
				            new InputStreamReader(echoSocket.getInputStream()));
				    
				    log.info("sending {}", messageSent);
				    
					out.println(messageSent);
					
					String messageRead = in.readLine();
					log.info("reading {}", messageRead);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	public static void main(String[] args) {
		runSample(new NettyComponentSample());
	}

}
