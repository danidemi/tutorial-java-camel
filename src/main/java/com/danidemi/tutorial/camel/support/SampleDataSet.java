package com.danidemi.tutorial.camel.support;

import static java.lang.String.format;

import org.apache.camel.component.dataset.DataSetSupport;

class SampleDataSet extends DataSetSupport {

	@Override
	protected Object createMessageBody(long messageIndex) {
		return format("This is msg %d", messageIndex);
	}
	
}