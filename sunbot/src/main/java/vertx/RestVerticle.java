package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
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
import types.MotorValue;
import types.SensorValue;

public class RestVerticle extends AbstractVerticle {
	
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
		router.get("/api/sensors").handler(this::getAllSensorData);
		router.get("/api/sensor/:sensorId").handler(this::getSensorValues);
		router.get("/api/getSensorValueById/:idsensor_value").handler(this::getSensorValueById);
		router.put("/api/putSensorValue").handler(this::addOneSensorValue);
		router.delete("/api/deleteSensorValue/:idsensor_value").handler(this::deleteOneSensorValue);
		router.post("/api/postSensorValue").handler(this::postOneSensorValue);
		
		router.get("/api/motors").handler(this::getAllMotorData);
		router.get("/api/motor/:motorId").handler(this::getMotorValues);
		router.get("/api/getMotorValueById/:idmotor_value").handler(this::getMotorValueById);
		router.put("/api/putMotorValue").handler(this::addOneMotorValue);
		router.delete("/api/deleteMotorValue/:idmotor_value").handler(this::deleteOneMotorValue);
		router.post("/api/postMotorValue").handler(this::postOneMotorValue);
		
		vertx.deployVerticle(MQTTServerVerticle.class.getName());
		//vertx.deployVerticle(MqttClientVerticle.class.getName());
	}
	
	////////////////
	/*Sensor Value*/
	////////////////
	private void getAllSensorData(RoutingContext routingContext) {
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
	
	private void getSensorValueById(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.sensor_value WHERE idsensor_value = " + 
				routingContext.request().getParam("idsensor_value"), 
		res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
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
					System.out.println("El objeto ya existe en la base de datos.");
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
		.end("Petición PUT ejecutada correctamente.");
	}
	private void deleteOneSensorValue(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.sensor_value WHERE idsensor_value="+routingContext.request().getParam("idsensor_value"), res->{
			if(res.succeeded()) {
				if(res.result().size()>0) {
					mySQLPool.query("DELETE FROM dad_sunbot.sensor_value WHERE idsensor_value="+routingContext.request().getParam("idsensor_value"), 
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
				.end("Petición DELETE ejecutada correctamente.");
	}
	private void postOneSensorValue(RoutingContext routingContext) {
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
					System.out.println("El objeto no existe en la base de datos.");
				}
			}
		});
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end("Petición POST ejecutada correctamente.");
	}
	
	
	////////////////
	/*Motor Value*/
	////////////////
	private void getAllMotorData(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.motor_value",
		res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println("El número de elementos obtenidos es " + resultSet.size());
				JsonArray result = new JsonArray();
				for (Row row : resultSet) {
					result.add(JsonObject.mapFrom(new MotorValue(
							row.getInteger("idmotor_value"),
							row.getInteger("idmotor"),
							row.getFloat("value"),
							row.getLong("timestamp"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
			}
		});
	}
	private void getMotorValues(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.motor_value WHERE idmotor = " + 
				routingContext.request().getParam("motorId"), 
		res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				System.out.println("El número de elementos obtenidos es " + resultSet.size());
				JsonArray result = new JsonArray();
				for (Row row : resultSet) {
					result.add(JsonObject.mapFrom(new MotorValue(
							row.getInteger("idmotor_value"),
							row.getInteger("idmotor"),
							row.getFloat("value"),
							row.getLong("timestamp"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
			}
		});
	}
	
	private void getMotorValueById(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.motor_value WHERE idmotor_value = " + 
				routingContext.request().getParam("idmotor_value"), 
		res -> {
			if (res.succeeded()) {
				RowSet<Row> resultSet = res.result();
				JsonArray result = new JsonArray();
				for (Row row : resultSet) {
					result.add(JsonObject.mapFrom(new MotorValue(
							row.getInteger("idmotor_value"),
							row.getInteger("idmotor"),
							row.getFloat("value"),
							row.getLong("timestamp"))));
				}
				routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
					.end(result.encodePrettily());
			}
		});
	}
	
	private void addOneMotorValue(RoutingContext routingContext) {
		JsonObject senval = routingContext.getBodyAsJson();
		mySQLPool.query("SELECT * FROM dad_sunbot.motor_value WHERE idmotor_value="+senval.getInteger("idmotor_value"), res->{
			if(res.succeeded()) {
				if(res.result().size()>0) {
					System.out.println("El objeto ya existe en la base de datos.");
				}else {
					mySQLPool.query("INSERT INTO motor_value(idmotor_value,idmotor,value,timestamp) VALUES("+
							senval.getInteger("idmotor_value") + ","+senval.getInteger("idmotor")+","+senval.getFloat("value")+","+senval.getInteger("timestamp")+")", 
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
		.end("Petición PUT ejecutada correctamente.");
	}
	private void deleteOneMotorValue(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM dad_sunbot.motor_value WHERE idmotor_value="+routingContext.request().getParam("idmotor_value"), res->{
			if(res.succeeded()) {
				if(res.result().size()>0) {
					mySQLPool.query("DELETE FROM dad_sunbot.motor_value WHERE idmotor_value="+routingContext.request().getParam("idmotor_value"), 
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
				.end("Petición DELETE ejecutada correctamente.");
	}
	private void postOneMotorValue(RoutingContext routingContext) {
		JsonObject senval = routingContext.getBodyAsJson();
		mySQLPool.query("SELECT * FROM dad_sunbot.motor_value WHERE idmotor_value="+senval.getInteger("idmotor_value"), res->{
			if(res.succeeded()) {
				if(res.result().size()>0) {
					mySQLPool.query("UPDATE dad_sunbot.motor_value SET idmotor="+senval.getInteger("idmotor")+", value="+senval.getFloat("value")+", timestamp="+senval.getInteger("timestamp")+
							" WHERE idmotor_value="+senval.getInteger("idmotor_value"), 
							res2 -> {
								if (res2.succeeded()) {
									System.out.println("Datos actualizados correctamente.");
								}else {
									System.out.println("Error en la actualización de los datos.");
								}
							});
				}else {
					System.out.println("El objeto no existe en la base de datos.");
				}
			}
		});
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end("Petición POST ejecutada correctamente.");
	}
	public void stop(Future<Void> stopFuture) throws Exception{
		super.stop(stopFuture);
	}
}
