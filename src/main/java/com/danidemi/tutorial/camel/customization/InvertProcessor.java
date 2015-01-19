package com.danidemi.tutorial.camel.customization;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultMessage;
import org.apache.commons.lang3.StringUtils;

public class InvertProcessor implements Processor {

	public void process(Exchange exchange) throws Exception {
		Message from = exchange.getIn();
		
		DefaultMessage m = new DefaultMessage();
		m.setBody( StringUtils.reverse( ((String)from.getBody()) ) );
		
		exchange.setIn( m );
		
	}

}
