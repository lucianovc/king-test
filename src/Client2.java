import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * Created by luciano on 1/11/16.
 */
public class Client2 {

    public static void main(String[] a) throws IOException, InterruptedException, ExecutionException {

        String sessionId = login(1);
        String sessionId2 = login(2);
        String sessionId3 = login(3);

        ExecutorService service = Executors.newCachedThreadPool();

        Callable<String> c1 = createCallable(sessionId, 100, 1);
        Callable<String> c2 = createCallable(sessionId2, 101, 1);
        Callable<String> c3 = createCallable(sessionId3, 102, 1);

        ArrayList<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            tasks.add(createCallable(sessionId, i, 1));
        }
        for (int i = 0; i < 10000; i++) {
            tasks.add(createCallable(sessionId2, i, 1));
        }
        service.invokeAll(tasks);
        printHighScores(1);

        service.shutdown();
    }

    private static Callable<String> createCallable(final String sessionId2, int points, int userid) {
        return new Callable<String>() {
                @Override
                public String call() throws Exception {
                    long n1 = System.nanoTime();
                    setScore(sessionId2, points, userid);
                    return (System.nanoTime() - n1)+Thread.currentThread().getName();
                }
            };
    }

    private static void setScore(String sessionId, int score, int level) throws IOException {
        URL url = new URL("http://localhost:8081/" + level + "/score?sessionkey=" + sessionId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.getOutputStream().write(String.valueOf(score).getBytes());
        int res = con.getResponseCode();
        //System.out.println("respon:"+res);
        con.disconnect();
    }

    private static void dump() throws IOException {
        URL url = new URL("http://localhost:8081/1/dump");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("GET");
        BufferedReader br = toBufferedStream(con);
        System.out.println("dump:" + br.readLine());
        con.disconnect();
    }

    private static String login(int userId) throws IOException {
        URL url = new URL("http://localhost:8081/" + userId + "/login");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int res = con.getResponseCode();
        //System.out.println("respon:"+res);

        BufferedReader reader = toBufferedStream(con);
        String response = reader.readLine();
//        System.out.println(response + "#" + Thread.currentThread().getName());
        reader.close();
        con.disconnect();
        return response;
    }

    private static BufferedReader toBufferedStream(HttpURLConnection con) throws IOException {
        return new BufferedReader(new InputStreamReader(con.getInputStream()));
    }

    private static void printHighScores(int level) throws IOException {
        URL url = new URL("http://localhost:8081/" + level + "/highscorelist");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int res = con.getResponseCode();
        //System.out.println("respon:"+res);

        BufferedReader reader = toBufferedStream(con);
        String response = reader.readLine();
        response = response == null ? "" : response;
        System.out.println(response + "#");
        con.disconnect();
        reader.close();
    }
}
