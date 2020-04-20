package vertx;

import java.util.List;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;

import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.ClientAuth;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;

public class MQTTServerVerticle extends AbstractVerticle {
	
	private static final String TOPIC_LIGHTS;
	private static final String TOPIC_INFO;
	private static final String TOPIC_DOMO;
	private static final SetMultimap<String, MqttEndpoint> clients = LinkedHashMultimap.create();
	
	public void start (Promise<Void> promise) {
		MqttServerOptions options = new MqttServerOptions();
		options.setPort(1885);
		options.setClientAuth(ClientAuth.REQUIRED);
		MqttServer mqttServer = MqttServer.create(vertx, options);
		init(mqttServer);
	}

	public void init(MqttServer mqttServer) {
		mqttServer.endpointHandler(endpoint -> {
			System.out.println("MQTT Client [" + endpoint.clientIdentifier()+"] request to connect, clean session = " + endpoint.isCleanSession());
			if (endpoint.auth().getUsername().contentEquals("mqttbroker") && endpoint.auth().getPassword().contentEquals("mqttbrokerpass")) {
				//USUARIO Y CONTRASEÑA CORRECTOS
				endpoint.accept();
				handleSubscription(endpoint);
				handleUnsubscription(endpoint);
				publishHandler(endpoint);
				handleSubscription(endpoint);
			}else {
				//AUTENTIFICACION NO CORRECTA
				endpoint.reject(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
			}
			
		}).listen(ar-> {
			if(ar.succeeded()) {
				System.out.println("MQTT server is listening on port"+ ar.result().actualPort());
			}else {
				System.out.println("Error on starting the MQTT server");
				ar.cause().printStackTrace();
			}
		});
	}
	private void handleSubscription(MqttEndpoint endpoint) {
		endpoint.subscribeHandler(subsribe ->{
			List<MqttQoS> grantedQoSLevels = new ArrayList();
		});
	}
}
