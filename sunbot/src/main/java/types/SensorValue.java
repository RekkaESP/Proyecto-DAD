package types;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorValue {
	private static final AtomicInteger COUNTER = new AtomicInteger();
	private int idsensor_value;
	private int idsensor;
	private float value;
	private float accuracy;
	private long timestamp;
	
	@JsonCreator
	public SensorValue(@JsonProperty("idsensor_value") int idsensor_value, @JsonProperty("idsensor") int idsensor, @JsonProperty("value") float value, @JsonProperty("accuracy") float accuracy, @JsonProperty("timestamp") long timestamp) {
		super();
		this.idsensor_value = idsensor_value;
		this.idsensor = idsensor;
		this.value = value;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
	}
	
	@JsonCreator
	public SensorValue(@JsonProperty("idsensor") int idsensor, @JsonProperty("value") float value, @JsonProperty("accuracy") float accuracy, @JsonProperty("timestamp") long timestamp) {
		super();
		this.idsensor_value = COUNTER.getAndIncrement();
		this.idsensor = idsensor;
		this.value = value;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
	}
	
	@JsonCreator
	public SensorValue(@JsonProperty("idsensor") int idsensor, @JsonProperty("value") float value, @JsonProperty("accuracy") float accuracy) {
		super();
		this.idsensor_value = COUNTER.getAndIncrement();
		this.idsensor = idsensor;
		this.value = value;
		this.accuracy = accuracy;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	@JsonCreator
	public SensorValue(@JsonProperty("idsensor") int idsensor) {
		super();
		this.idsensor_value = COUNTER.getAndIncrement();
		this.idsensor = idsensor;
		this.value = 0;
		this.accuracy = 0;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	@JsonCreator
	public SensorValue() {
		super();
		this.idsensor_value = COUNTER.getAndIncrement();
		this.idsensor = 0;
		this.value = 0;
		this.accuracy = 0;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}

	public int getIdsensor_value() {
		return idsensor_value;
	}

	public void setIdsensor_value(int idsensor_value) {
		this.idsensor_value = idsensor_value;
	}

	public int getIdsensor() {
		return idsensor;
	}

	public void setIdsensor(int idsensor) {
		this.idsensor = idsensor;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(accuracy);
		result = prime * result + idsensor;
		result = prime * result + idsensor_value;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + Float.floatToIntBits(value);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorValue other = (SensorValue) obj;
		if (Float.floatToIntBits(accuracy) != Float.floatToIntBits(other.accuracy))
			return false;
		if (idsensor != other.idsensor)
			return false;
		if (idsensor_value != other.idsensor_value)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SensorValue [idsensor_value=" + idsensor_value + ", idsensor=" + idsensor + ", value=" + value
				+ ", accuracy=" + accuracy + ", timestamp=" + timestamp + "]";
	}
	
	
}
