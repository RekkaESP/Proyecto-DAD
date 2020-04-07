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
		Humidity h = new Humidity(75, 8);
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
		final Humidity hum = Json.decodeValue(routingContext.getBodyAsString(), Humidity.class);
		humidities.put(hum.getId(), hum);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(humidities));
	}
	private void deleteOneHumidity(RoutingContext routingContext) {
		final Humidity hum = Json.decodeValue(routingContext.getBodyAsString(), Humidity.class);
		humidities.remove(hum.getId());
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encodePrettily(humidities));
	}
	private void postOneHumidity(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("elementid"));
		Humidity new_hum = humidities.get(id);
		final Humidity hum = Json.decodeValue(routingContext.getBodyAsString(), Humidity.class);
		new_hum.setHumidityLevel(hum.getHumidityLevel());
		new_hum.setTimestamp(hum.getTimestamp());
		humidities.put(hum.getId(), hum);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
				.end(Json.encode(humidities));
	}
	///////////////
	/*Temperature*/
	///////////////
	private void getAllTemperatures(RoutingContext routingContext) {
        routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(temperatures.values()));
    }
    private void addOneTemperature(RoutingContext routingContext) {
        final Temperature temp = Json.decodeValue(routingContext.getBodyAsString(), Temperature.class);
        temperatures.put(temp.getId(), temp);
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(temp));
    }
    private void deleteOneTemperature(RoutingContext routingContext) {
        final Temperature temp = Json.decodeValue(routingContext.getBodyAsString(), Temperature.class);
        temperatures.remove(temp.getId());
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(temp));
    }
    private void postOneTemperature(RoutingContext routingContext) {
        int id = Integer.parseInt(routingContext.request().getParam("tempId"));
        Temperature new_temp = temperatures.get(id);
        final Temperature temp = Json.decodeValue(routingContext.getBodyAsString(), Temperature.class);
        new_temp.setTemperatureLevel(temp.getTemperatureLevel());
        new_temp.setTimestamp(temp.getTimestamp());
        temperatures.put(new_temp.getId(), new_temp);
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8").end(Json.encode(temp));
    }
	//////////////
	/*Luminosity*/
	//////////////
	private void getAllLuminosities(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(luminosities.values()));
	}
	private void addOneLuminosity(RoutingContext routingContext) {
		final Luminosity lum = Json.decodeValue(routingContext.getBodyAsString(), Luminosity.class);
        luminosities.put(lum.getId(), lum);
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(lum));
	}
	private void deleteOneLuminosity(RoutingContext routingContext) {
		final Luminosity lum = Json.decodeValue(routingContext.getBodyAsString(), Luminosity.class);
        luminosities.remove(lum.getId());
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
                .end(Json.encodePrettily(lum));
	}
	private void postOneLuminosity(RoutingContext routingContext) {
		int id = Integer.parseInt(routingContext.request().getParam("tempId"));
        Luminosity new_lum = luminosities.get(id);
        final Luminosity lum = Json.decodeValue(routingContext.getBodyAsString(), Luminosity.class);
        new_lum.setLuminosityLevel(lum.getLuminosityLevel());
        new_lum.setTimestamp(lum.getTimestamp());
        luminosities.put(new_lum.getId(), new_lum);
        routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8").end(Json.encode(lum));
	}
	
	public void stop(Future<Void> stopFuture) throws Exception{
		super.stop(stopFuture);
	}
}
