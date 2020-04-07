package types;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Luminosity {
	private static final AtomicInteger COUNTER = new AtomicInteger();
	private int id;
	private float luminosityLevel;
	private long timestamp;
	private float accuracy;
	
	@JsonCreator
	public Luminosity(@JsonProperty("luminosityLevel") float luminosityLevel,@JsonProperty("timestamp") long timestamp, @JsonProperty("accuracy") float accuracy) {
		super();
		this.id = COUNTER.getAndIncrement();
		this.luminosityLevel = luminosityLevel;
		this.accuracy = accuracy;
		this.timestamp = timestamp;
	}
	
	public Luminosity(@JsonProperty("luminosityLevel") float luminosityLevel, @JsonProperty("accuracy") float accuracy) {
		super();
		this.id = COUNTER.getAndIncrement();
		this.luminosityLevel = luminosityLevel;
		this.accuracy = accuracy;
		this.timestamp = Calendar.getInstance().getTimeInMillis();
	}
	
	public Luminosity() {
		super();
		this.id = COUNTER.getAndIncrement();
		this.luminosityLevel = 0;
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

	public float getLuminosityLevel() {
		return luminosityLevel;
	}

	public void setLuminosityLevel(float luminosityLevel) {
		this.luminosityLevel = luminosityLevel;
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
		result = prime * result + Float.floatToIntBits(luminosityLevel);
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
		Luminosity other = (Luminosity) obj;
		if (id != other.id)
			return false;
		if (Float.floatToIntBits(luminosityLevel) != Float.floatToIntBits(other.luminosityLevel))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Luminosity [id=" + id + ", luminosityLevel=" + luminosityLevel + ", timestamp=" + timestamp + "]";
	}
	
}
