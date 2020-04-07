package vertx;

import types.SensorValue;
import types.BotActions;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestVerticle extends AbstractVerticle {
	
	private Map<Integer, SensorValue> sensorvalues = new LinkedHashMap<>();

	@Override
	public void start(Future<Void> startFuture) {
		createSomeData();
		System.out.println("Datos creados.");
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::accept).listen(8090, result->{});
		//router.route("/api/sensors").handler(this::getAllData);
		/*Sensor Value*/
		router.get("/api/humidity").handler(this::getAllSensorValue);
		router.put("/api/humidity").handler(this::addOneSensorValue);
		router.delete("/api/humidity").handler(this::deleteOneSensorValue);
		router.post("/api/humidity").handler(this::postOneSensorValue);

	}
	
	private void createSomeData() {

	}
	/*
	private void getAllData(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
		.end(Json.encodePrettily(sensorvalues.values())+"\n\n"+Json.encodePrettily(temperatures.values())+"\n\n"+Json.encodePrettily(luminosities.values()));
	}
	*/
	////////////////
	/*Sensor Value*/
	////////////////
	private void getAllSensorValue(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
		.end(Json.encodePrettily(sensorvalues.values()));
	}
	private void addOneSensorValue(RoutingContext routingContext) {
		final SensorValue senval = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		sensorvalues.put(senval.getId(), senval);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(sensorvalues));
	}
	private void deleteOneSensorValue(RoutingContext routingContext) {
		final SensorValue senval = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		sensorvalues.remove(senval.getId());
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(sensorvalues));
	}
	private void postOneSensorValue(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("elementid"));
		SensorValue new_senval = sensorvalues.get(id);
		final SensorValue senval = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		new_senval.setType(senval.getType());
		new_senval.setValue(senval.getValue());
		new_senval.setAccuracy(senval.getAccuracy());
		new_senval.setTimestamp(senval.getTimestamp());
		sensorvalues.put(senval.getId(), senval);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encode(sensorvalues));
	}
	public void stop(Future<Void> stopFuture) throws Exception{
		super.stop(stopFuture);
	}
}
