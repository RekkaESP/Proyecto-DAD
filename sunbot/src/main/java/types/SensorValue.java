package types;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorValue {
	private static final AtomicInteger COUNTER = new AtomicInteger();
	private enum Type{Humidity, Temperature, Luminosity};
	private Type type;
	private int id;
	private float value;
	private float accuracy;
	private long timestamp;
	
	@JsonCreator
	public SensorValue(@JsonProperty("type") Type type, @JsonProperty("value") float value, @JsonProperty("accuracy") float accuracy, @JsonProperty("timestamp") long timestamp) {
		super();
		this.type = type;
		this.id = COUNTER.getAndIncrement();
		this.value = value;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
	}
	
	public SensorValue(@JsonProperty("type") Type type, @JsonProperty("value") float value, @JsonProperty("accuracy") float accuracy) {
		super();
		this.type = type;
		this.id = COUNTER.getAndIncrement();
		this.value = value;
		this.accuracy = accuracy;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public SensorValue(@JsonProperty("type") Type type) {
		super();
		this.type = type;
		this.id = COUNTER.getAndIncrement();
		this.value = 0;
		this.accuracy = 0;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
		result = prime * result + id;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		if (id != other.id)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (type != other.type)
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SensorValue [type=" + type + ", id=" + id + ", value=" + value + ", accuracy=" + accuracy
				+ ", timestamp=" + timestamp + "]";
	}
	
	
	
}
