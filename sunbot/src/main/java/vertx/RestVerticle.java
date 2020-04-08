package vertx;

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
import types.SensorValue;

public class RestVerticle extends AbstractVerticle {
	
	private Map<Integer, SensorValue> sensorvalues = new LinkedHashMap<>();
	private MySQLPool mySQLPool;
	@SuppressWarnings("deprecation")
	@Override
	public void start(Future<Void> startFuture) {
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("dad_sunbot").setUser("root").setPassword("1234");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::accept).listen(8090, result->{});
		router.route("/*").handler(BodyHandler.create());
		router.route("/api/sensors").handler(this::getAllData);
		router.get("/api/:sensorId").handler(this::getSensorValues);
		router.put("/api/putSensorValue").handler(this::addOneSensorValue);
		router.delete("/api/deleteSensorValue").handler(this::deleteOneSensorValue);
		router.post("/api/postSensorValue").handler(this::postOneSensorValue);
	}
	
	////////////////
	/*Sensor Value*/
	////////////////
	private void getAllData(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.sensor_value",
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
	private void getSensorValues(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.sensor_value WHERE idsensor = " + 
				routingContext.request().getParam("sensorId"), 
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
		JsonObject senval = routingContext.getBodyAsJson();
		mySQLPool.query("SELECT * FROM dad_sunbot.sensor_value WHERE idsensor_value="+senval.getInteger("idsensor_value"), res->{
			if(res.succeeded()) {
				if(res.result().size()>0) {
					mySQLPool.query("UPDATE dad_sunbot.sensor_value SET idsensor="+senval.getInteger("idsensor")+", value="+senval.getFloat("value")+", accuracy="+senval.getFloat("accuracy")+
							", timestamp="+senval.getInteger("timestamp")+" WHERE idsensor_value="+senval.getInteger("idsensor_value"), 
						res2 -> {
							if (res2.succeeded()) {
								System.out.println("Datos actualizados correctamente.");
							}else {
								System.out.println("Error en la actualización de los datos.");
							}
						});
				}else {
					mySQLPool.query("INSERT INTO sensor_value(idsensor_value,idsensor,value,accuracy,timestamp) VALUES("+
							senval.getInteger("idsensor_value") + ","+senval.getInteger("idsensor")+","+senval.getFloat("value")+","+senval.getFloat("accuracy")+","+senval.getInteger("timestamp")+")", 
							res2 -> {
								if (res2.succeeded()) {
									System.out.println("Datos introducidos correctamente.");
								}else {
									System.out.println("Error al introducir los datos");
								}
							});
				}
			}
		});
		routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
		.end();
	}
	private void deleteOneSensorValue(RoutingContext routingContext) {
		JsonObject senval = routingContext.getBodyAsJson();
		mySQLPool.query("SELECT * FROM dad_sunbot.sensor_value WHERE idsensor_value="+senval.getInteger("idsensor_value"), res->{
			if(res.succeeded()) {
				if(res.result().size()>0) {
					mySQLPool.query("DELETE FROM dad_sunbot.sensor_value WHERE idsensor_value="+senval.getInteger("idsensor_value"), 
						res2 -> {
							if (res2.succeeded()) {
								System.out.println("Datos actualizados correctamente.");
							}else {
								System.out.println("Error en la actualización de los datos.");
							}
						});
				}else {
					System.out.println("No existe el elemento.");
				}
			}
		});
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end();
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
