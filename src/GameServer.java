import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luciano on 1/11/16.
 */
public class GameServer {

    private HttpServer server;
    private HttpHandler handler;

    public void start() throws IOException {
        server = HttpServer.create();
        server.bind(new InetSocketAddress("localhost", 8081), 0);

        handler = new GameHTTPHandler();
        HttpContext ctx = server.createContext("/", handler);
        int nThreads = Runtime.getRuntime().availableProcessors() + 1;
        System.out.println("num of threads: "+nThreads);
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        server.setExecutor(executor);
        server.start();
        System.out.println(Thread.currentThread().getName());

    }

    public static void main(String[] a) throws IOException {
        GameServer server = new GameServer();
        server.start();
    }
}
