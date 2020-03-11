package types;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Humidity {
	private static final AtomicInteger COUNTER = new AtomicInteger();
	private int id;
	private float humidityLevel;
	private long timestamp;
	
	@JsonCreator
	public Humidity(@JsonProperty("humidityLevel") float humidityLevel,@JsonProperty("timestamp") long timestamp) {
		super();
		this.id = COUNTER.getAndIncrement();
		this.humidityLevel = humidityLevel;
		this.timestamp = timestamp;
	}
	
	public Humidity(@JsonProperty("humidityLevel") float humidityLevel) {
		super();
		this.id = COUNTER.getAndIncrement();
		this.humidityLevel = humidityLevel;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public Humidity() {
		super();
		this.id = COUNTER.getAndIncrement();
		this.humidityLevel = 0;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getHumidityLevel() {
		return humidityLevel;
	}

	public void setHumidityLevel(float humidityLevel) {
		this.humidityLevel = humidityLevel;
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
		result = prime * result + Float.floatToIntBits(humidityLevel);
		result = prime * result + id;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
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
		Humidity other = (Humidity) obj;
		if (Float.floatToIntBits(humidityLevel) != Float.floatToIntBits(other.humidityLevel))
			return false;
		if (id != other.id)
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Humidity [id=" + id + ", humidityLevel=" + humidityLevel + ", timestamp=" + timestamp + "]";
	}
	
	
}
