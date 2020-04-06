package vertx;

import types.Temperature;
import types.Humidity;
import types.Luminosity;
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
	private Map<Integer, Temperature> temperatures = new LinkedHashMap<>();
	private Map<Integer, Humidity> humidities = new LinkedHashMap<>();
	private Map<Integer, Luminosity> luminosities = new LinkedHashMap<>();
	
	@Override
	public void start(Future<Void> startFuture) {
		createSomeData();
		System.out.println("Datos creados.");
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::accept).listen(8090, result->{});
		router.route("/api/sensors").handler(this::getAllData);
		/*Humidity*/
		router.get("/api/humidity").handler(this::getAllHumidities);
		router.put("/api/humidity").handler(this::addOneHumidity);
		router.delete("/api/humidity").handler(this::deleteOneHumidity);
		router.post("/api/humidity").handler(this::postOneHumidity);
		/*Temperature*/
		router.get("/api/temperature").handler(this::getAllTemperatures);
		router.put("/api/temperature").handler(this::addOneTemperature);
		router.delete("/api/temperature").handler(this::deleteOneTemperature);
		router.post("/api/temperature").handler(this::postOneTemperature);
		/*Luminosity*/
		router.get("/api/luminosity").handler(this::getAllLuminosities);		
		router.put("/api/luminosity").handler(this::addOneLuminosity);		
		router.delete("/api/luminosity").handler(this::deleteOneLuminosity);		
		router.post("/api/luminosity").handler(this::postOneLuminosity);
	}
	
	private void createSomeData() {
		Humidity h = new Humidity(75);
		humidities.put(h.getId(), h);
		
		Temperature t = new Temperature(26);
		temperatures.put(t.getId(), t);
		
		Luminosity l = new Luminosity(640);
		luminosities.put(l.getId(), l);
	}
	
	private void getAllData(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
		.end(Json.encodePrettily(humidities.values())+"\n\n"+Json.encodePrettily(temperatures.values())+"\n\n"+Json.encodePrettily(luminosities.values()));
	}
	////////////
	/*Humidity*/
	////////////
	private void getAllHumidities(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
		.end(Json.encodePrettily(humidities.values()));
	}
	private void addOneHumidity(RoutingContext routingContext) {
		
	}
	private void deleteOneHumidity(RoutingContext routingContext) {
		
	}
	private void postOneHumidity(RoutingContext routingContext) {
		
	}
	///////////////
	/*Temperature*/
	///////////////
	private void getAllTemperatures(RoutingContext routingContext) {
		
	}
	private void addOneTemperature(RoutingContext routingContext) {
		
	}
	private void deleteOneTemperature(RoutingContext routingContext) {
		
	}
	private void postOneTemperature(RoutingContext routingContext) {
		
	}
	//////////////
	/*Luminosity*/
	//////////////
	private void getAllLuminosities(RoutingContext routingContext) {
		
	}
	private void addOneLuminosity(RoutingContext routingContext) {
		
	}
	private void deleteOneLuminosity(RoutingContext routingContext) {
		
	}
	private void postOneLuminosity(RoutingContext routingContext) {
		
	}
	
	public void stop(Future<Void> stopFuture) throws Exception{
		super.stop(stopFuture);
	}
}
