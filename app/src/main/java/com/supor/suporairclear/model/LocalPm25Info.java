package com.supor.suporairclear.model;

/**
 * 城市属性实体类
 * @author sy
 *
 */
public class LocalPm25Info
{
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

	public long getMin() {
		return min;
	}

	public void setMin(long min) {
		this.min = min;
	}

	public long getMax() {
		return max;
	}

	public void setMax(long max) {
		this.max = max;
	}

	private String timestamp;
	private long value;
	private long min;
	private long max;

	public LocalPm25Info(long min, long value,String timestamp, long max) {
		super();
		this.timestamp = timestamp;
		this.value = value;
		this.max = max;
		this.min = min;
	}


}
