package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import types.SensorValue;

public class DatabaseVerticle extends AbstractVerticle{
	
	private MySQLPool mySQLPool;
	
	@Override
	public void start(Promise<Void> startPromise) {
		MySQLConnectOptions mySQLConnectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("daddatabase").setUser("root").setPassword("root");
		PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
		mySQLPool = MySQLPool.pool(vertx, mySQLConnectOptions, poolOptions);
		
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startPromise.complete();
			}else {
				startPromise.fail(result.cause());
			}
		});
		router.get("/api/sensor/values/:idSensor/:timestamp").handler(this::getValueBySensorAndTimestamp);
		router.get("/api/sensor/values/:idSensor").handler(this::getValueBySensor);
	}
	
	private void getValueBySensorAndTimestamp(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM daddatabase.sensor_value WHERE timestamp > " + 
						routingContext.request().getParam("timestamp") + " AND idsensor = " + 
						routingContext.request().getParam("idSensor"), 
				res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El número de elementos obtenidos es " + resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new SensorValue(row.getInteger("idsensor_value"),
									row.getInteger("idsensor"),
									row.getFloat("value"),
									row.getFloat("accuracy"),
									row.getLong("timestamp"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
							.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}
	
	private void getValueBySensor(RoutingContext routingContext) {
		mySQLPool.query("SELECT * FROM daddatabase.sensor_value WHERE idsensor = " + routingContext.request().getParam("idSensor"), 
				res -> {
					if (res.succeeded()) {
						RowSet<Row> resultSet = res.result();
						System.out.println("El número de elementos obtenidos es " + resultSet.size());
						JsonArray result = new JsonArray();
						for (Row row : resultSet) {
							result.add(JsonObject.mapFrom(new SensorValue(row.getInteger("idsensor_value"),
									row.getInteger("idsensor"),
									row.getFloat("value"),
									row.getFloat("accuracy"),
									row.getLong("timestamp"))));
						}
						
						routingContext.response().setStatusCode(200).putHeader("content-type", "application/json")
							.end(result.encodePrettily());
					}else {
						routingContext.response().setStatusCode(401).putHeader("content-type", "application/json")
							.end((JsonObject.mapFrom(res.cause()).encodePrettily()));
					}
				});
	}

}
