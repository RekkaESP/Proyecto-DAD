package vertx;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.mqtt.messages.MqttPublishMessage;

public class MqttMessageFormat {

	private String receiverId;
	private String topic;
	private int messageId;
	private boolean isDup;
	private boolean isRetain;
	private String content;
	private String qosLevel;

	public MqttMessageFormat(String receiverId, MqttPublishMessage mqttPublishMessage) {
		this.receiverId = receiverId;
		this.topic = mqttPublishMessage.topicName();
		this.messageId = mqttPublishMessage.messageId();
		this.isDup = mqttPublishMessage.isDup();
		this.isRetain = mqttPublishMessage.isRetain();
		this.content = mqttPublishMessage.payload().toString();
		this.qosLevel = mqttPublishMessage.qosLevel().name();
	}

	public MqttMessageFormat() {
		this.receiverId = "";
		this.topic = "";
		this.messageId = 0;
		this.isDup = false;
		this.isRetain = false;
		this.content = "";
		this.qosLevel = MqttQoS.FAILURE.name();
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public boolean isDup() {
		return isDup;
	}

	public void setDup(boolean isDup) {
		this.isDup = isDup;
	}

	public boolean isRetain() {
		return isRetain;
	}

	public void setRetain(boolean isRetain) {
		this.isRetain = isRetain;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getQosLevel() {
		return qosLevel;
	}

	public void setQosLevel(String qosLevel) {
		this.qosLevel = qosLevel;
	}

	@Override
	public String toString() {
		return "MqttMessageFormat [receiverId=" + receiverId + ", topic=" + topic + ", messageId=" + messageId
				+ ", isDup=" + isDup + ", isRetain=" + isRetain + ", content=" + content + ", qosLevel=" + qosLevel
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + (isDup ? 1231 : 1237);
		result = prime * result + (isRetain ? 1231 : 1237);
		result = prime * result + messageId;
		result = prime * result + ((qosLevel == null) ? 0 : qosLevel.hashCode());
		result = prime * result + ((receiverId == null) ? 0 : receiverId.hashCode());
		result = prime * result + ((topic == null) ? 0 : topic.hashCode());
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
		MqttMessageFormat other = (MqttMessageFormat) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (isDup != other.isDup)
			return false;
		if (isRetain != other.isRetain)
			return false;
		if (messageId != other.messageId)
			return false;
		if (qosLevel == null) {
			if (other.qosLevel != null)
				return false;
		} else if (!qosLevel.equals(other.qosLevel))
			return false;
		if (receiverId == null) {
			if (other.receiverId != null)
				return false;
		} else if (!receiverId.equals(other.receiverId))
			return false;
		if (topic == null) {
			if (other.topic != null)
				return false;
		} else if (!topic.equals(other.topic))
			return false;
		return true;
	}
	
	

}
