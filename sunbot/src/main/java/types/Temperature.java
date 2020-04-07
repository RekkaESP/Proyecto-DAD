package types;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Temperature {
	private static final AtomicInteger COUNTER = new AtomicInteger();
	private int id;
	private float temperatureLevel;
	private float accuracy;
	private long timestamp;
	
	@JsonCreator
	public Temperature(@JsonProperty("temperatureLevel") float temperatureLevel,@JsonProperty("accuracy") float accuracy,@JsonProperty("timestamp") long timestamp) {
		super();
		this.id = COUNTER.getAndIncrement();
		this.temperatureLevel = temperatureLevel;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
	}
	
	public Temperature(@JsonProperty("temperatureLevel") float temperatureLevel,@JsonProperty("accuracy") float accuracy) {
		super();
		this.id = COUNTER.getAndIncrement();
		this.temperatureLevel = temperatureLevel;
		this.accuracy = accuracy;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public Temperature() {
		super();
		this.id = COUNTER.getAndIncrement();
		this.temperatureLevel = 0;
		this.accuracy = 0;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getTemperatureLevel() {
		return temperatureLevel;
	}

	public void setTemperatureLevel(float temperatureLevel) {
		this.temperatureLevel = temperatureLevel;
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
		result = prime * result + id;
		result = prime * result + Float.floatToIntBits(temperatureLevel);
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
		Temperature other = (Temperature) obj;
		if (id != other.id)
			return false;
		if (Float.floatToIntBits(temperatureLevel) != Float.floatToIntBits(other.temperatureLevel))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Temperature [id=" + id + ", temperatureLevel=" + temperatureLevel + ", timestamp=" + timestamp + "]";
	}
	
	
}
