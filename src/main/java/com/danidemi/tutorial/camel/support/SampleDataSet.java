package com.danidemi.tutorial.camel.support;

import static java.lang.String.format;

import org.apache.camel.component.dataset.DataSetSupport;

public class SampleDataSet extends DataSetSupport {
	
	public SampleDataSet(int size) {
		super(size);
	}

	@Override
	protected Object createMessageBody(long messageIndex) {
		return format("This is msg %d", messageIndex);
	}
	
}