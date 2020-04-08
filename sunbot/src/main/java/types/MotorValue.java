package types;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MotorValue {
	private static final AtomicInteger COUNTER = new AtomicInteger();
	private int idmotor_value;
	private int idmotor;
	private float value;
	private long timestamp;
	
	@JsonCreator
	public MotorValue(@JsonProperty("idmotor_value") int idmotor_value,@JsonProperty("idmotor") int idmotor,@JsonProperty("value") float value,@JsonProperty("timestamp") long timestamp) {
		super();
		this.idmotor_value = idmotor_value;
		this.idmotor = idmotor;
		this.value = value;
		this.timestamp = timestamp;
	}
	@JsonCreator
	public MotorValue(@JsonProperty("idmotor") int idmotor,@JsonProperty("value") float value,@JsonProperty("timestamp") long timestamp) {
		super();
		this.idmotor_value = COUNTER.getAndIncrement();
		this.idmotor = idmotor;
		this.value = value;
		this.timestamp = timestamp;
	}
	public MotorValue(@JsonProperty("idmotor") int idmotor,@JsonProperty("value") float value) {
		super();
		this.idmotor_value = COUNTER.getAndIncrement();
		this.idmotor = idmotor;
		this.value = value;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	@JsonCreator
	public MotorValue(@JsonProperty("idmotor") int idmotor) {
		super();
		this.idmotor_value = COUNTER.getAndIncrement();
		this.idmotor = idmotor;
		this.value = 0;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idmotor;
		result = prime * result + idmotor_value;
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
		MotorValue other = (MotorValue) obj;
		if (idmotor != other.idmotor)
			return false;
		if (idmotor_value != other.idmotor_value)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}
	public int getIdmotor_value() {
		return idmotor_value;
	}
	public void setIdmotor_value(int idmotor_value) {
		this.idmotor_value = idmotor_value;
	}
	public int getIdmotor() {
		return idmotor;
	}
	public void setIdmotor(int idmotor) {
		this.idmotor = idmotor;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
