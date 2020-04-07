package vertx;

import types.SensorValue;
import types.BotActions;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public class RestVerticle extends AbstractVerticle {
	
	private Map<Integer, SensorValue> sensorvalues = new LinkedHashMap<>();
	private MySQLPool mySQLPool;
	@Override
	public void start(Future<Void> startFuture) {
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad_sunbot").setUser("root").setPassword("1234");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);
		System.out.println("Datos creados.");
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::accept).listen(8090, result->{});
		//router.route("/api/sensors").handler(this::getAllData);
		/*Sensor Value*/
		router.get("/api/:sensorId").handler(this::getSensorValues);
		router.put("/api/:sensorId").handler(this::addOneSensorValue);
		router.delete("/api/:sensorId").handler(this::deleteOneSensorValue);
		router.post("/api/:sensorId").handler(this::postOneSensorValue);
	}
	
	////////////////
	/*Sensor Value*/
	////////////////
	private void getSensorValues(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.sensor_value WHERE idsensor = " + 
				routingContext.request().getParam("idSensor"), 
		res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println("El número de elementos obtenidos es " + resultSet.size());
				JsonArray result = new JsonArray();
				for (Row row : resultSet) {
					result.add(JsonObject.mapFrom(new SensorValue(
							row.getInteger("idsensor_value"),
							row.getInteger("idsensor"),
							row.getFloat("value"),
							row.getFloat("accuracy"),
							row.getLong("timestamp"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
			}
		});
	}
	private void addOneSensorValue(RoutingContext routingContext) {
		final SensorValue senval = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		sensorvalues.put(senval.getIdsensor_value(), senval);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(sensorvalues));
	}
	private void deleteOneSensorValue(RoutingContext routingContext) {
		final SensorValue senval = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		sensorvalues.remove(senval.getIdsensor_value());
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(sensorvalues));
	}
	private void postOneSensorValue(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("elementid"));
		SensorValue new_senval = sensorvalues.get(id);
		final SensorValue senval = Json.decodeValue(routingContext.getBodyAsString(), SensorValue.class);
		new_senval.setIdsensor(senval.getIdsensor());
		new_senval.setValue(senval.getValue());
		new_senval.setAccuracy(senval.getAccuracy());
		new_senval.setTimestamp(senval.getTimestamp());
		sensorvalues.put(senval.getIdsensor_value(), senval);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encode(sensorvalues));
	}
	public void stop(Future<Void> stopFuture) throws Exception{
		super.stop(stopFuture);
	}
}
