package vertx;

import types.Temperature;
import types.Humidity;
import types.Luminosity;
import types.BotActions;

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
		Router router = Router.router(vertx);
		vertx.createHttpServer().requestHandler(router::accept).listen(8090, result->{});
		router.route("/api/sensors*").handler(BodyHandler.create());
		router.get("/api/humidity").handler(this::getAllHumidities);
		router.get("/api/temperature").handler(this::getAllTemperatures);
		router.get("/api/luminosity").handler(this::getAllLuminosities);
		router.put("/api/humidity").handler(this::addOneHumidity);
		router.put("/api/temperature").handler(this::addOneTemperature);
		router.put("/api/luminosity").handler(this::addOneLuminosity);
		router.delete("/api/humidity").handler(this::deleteOneHumidity);
		router.delete("/api/temperature").handler(this::deleteOneTemperature);
		router.delete("/api/luminosity").handler(this::deleteOneLuminosity);
		router.post("/api/humidity").handler(this::postOneHumidity);
		router.post("/api/temperature").handler(this::postOneTemperature);
		router.post("/api/luminosity").handler(this::postOneLuminosity);
	}
	private void getAll(RoutingContext routingContext) {
		
	}
	private void addOne(RoutingContext routingContext) {
		
	}
	private void deleteOne(RoutingContext routingContext) {
		
	}
	private void postOne(RoutingContext routingContext) {
		
	}
	public void stop(Future<Void> stopFuture) throws Exception{
		super.stop(stopFuture);
	}
}
