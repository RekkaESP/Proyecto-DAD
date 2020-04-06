package vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestVerticle extends AbstractVerticle {
 public void start(Future<Void> startFuture) {
	 Router router = Router.router(vertx);
	 vertx.createHttpServer().requestHandler(router::accept).listen(8090, result->{});
	 router.route("/api/elements*").handler(BodyHandler.create());
	 router.get("/api/elements").handler(this::getAll);
	 router.put("/api/elements").handler(this::addOne);
	 router.delete("/api/elements").handler(this::deleteOne);
	 router.post("/api/elements").handler(this::postOne);
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
