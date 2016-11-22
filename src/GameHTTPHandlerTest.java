import com.sun.net.httpserver.HttpExchange;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by luciano on 8/11/16.
 */
public class GameHTTPHandlerTest {

    GameHTTPHandler handler;

    @Mock
    GameBackend backend;

    @Mock
    HttpExchange exchange;

    @Mock
    OutputStream out;

    @Mock
    InputStream in;

    @Before
    public void init(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLogin() throws InterruptedException, IOException, URISyntaxException {
        URI uri = new URI("/1/login");
        Mockito.when(exchange.getRequestURI()).thenReturn(uri);
        String response = "123123";
        Mockito.when(backend.createSessionId(1)).thenReturn(response);
        Mockito.when(exchange.getResponseBody()).thenReturn(out);

        handler = new GameHTTPHandler();
        handler.setBackend(backend);
        handler.handle(exchange);

        Mockito.verify(exchange).sendResponseHeaders(200, response.length());
        Mockito.verify(out).write(response.getBytes());
    }

    @Test
    public void testScore() throws InterruptedException, IOException, URISyntaxException {
        String session = "123123";
        URI uri = new URI("/1/score?sessionkey="+session);
        Mockito.when(exchange.getRequestURI()).thenReturn(uri);
        Mockito.when(backend.setUserScoreInLevel(1, session, 150)).thenReturn(true);
        Mockito.when(exchange.getResponseBody()).thenReturn(out);
        Mockito.when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream("150".getBytes()));

        handler = new GameHTTPHandler();
        handler.setBackend(backend);
        handler.handle(exchange);

        String response = "";
        Mockito.verify(exchange).sendResponseHeaders(200, response.length());
        Mockito.verify(out).write(response.getBytes());
    }

    @Test
    public void testHighscore() throws InterruptedException, IOException, URISyntaxException {
        String session = "123123";
        URI uri = new URI("/1/highscorelist");
        Mockito.when(exchange.getRequestURI()).thenReturn(uri);
        String response = "1=110,2=99";
        Mockito.when(backend.getHighscores(1)).thenReturn(response);
        Mockito.when(exchange.getResponseBody()).thenReturn(out);

        handler = new GameHTTPHandler();
        handler.setBackend(backend);
        handler.handle(exchange);

        Mockito.verify(exchange).sendResponseHeaders(200, response.length());
        Mockito.verify(out).write(response.getBytes());
    }
}
