import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created by luciano on 8/11/16.
 */
public class GameHTTPHandler implements HttpHandler {

    private GameBackend backend;

    public GameHTTPHandler() {
        this.backend = new GameBackend();
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        System.out.println(Thread.currentThread().getName());

        String response = "";
        int responseCode = 200;

        try {

            URI uri = httpExchange.getRequestURI();
            String[] params = uri.getPath().split("/");

            Integer idValue = Integer.valueOf(params[1]);
            String op = params[2];


            if (op.equals("login")) {
                response = backend.createSessionId(idValue);

            } else if (op.equals("highscorelist")) {
                response = backend.getHighscores(idValue);

            } else if (op.equals("score")) {
                String[] query = uri.getQuery().split("=");
                String paramName = query[0];
                String sessionKey = query[0];

                if (sessionKey.equals("sessionkey")) {

                    String sessionValue = query[1];
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()));
                    int scoreValue = Integer.valueOf(reader.readLine());
                    reader.close();
                    boolean success = backend.setUserScoreInLevel(idValue, sessionValue, scoreValue);
                    responseCode = success ? 200 : 500;
                    response = "";

                } else {

                    responseCode = 500;
                    response = "";
                }

            } else if (op.equals("dump")) {
                response = backend.getDump();
            }

        } catch (Exception e) {
            response = "";
            responseCode = 500;
        }

        System.out.println(response);
        sendResponse(httpExchange, response, responseCode);

    }

    private void sendResponse(HttpExchange httpExchange, String response, int responseCode) throws IOException {
        byte[] arr = response.getBytes();
        httpExchange.sendResponseHeaders(responseCode, arr.length);

        OutputStream responseBody = httpExchange.getResponseBody();
        responseBody.write(arr);
        responseBody.flush();
        responseBody.close();
    }

    public void setBackend(GameBackend backend) {
        this.backend = backend;
    }


}
