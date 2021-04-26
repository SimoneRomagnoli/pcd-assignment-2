
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        AbstractVerticle myVerticle = new WebService(8081);
        vertx.deployVerticle(myVerticle);
    }
}
