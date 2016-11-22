import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by luciano on 1/11/16.
 */
public class Client4 {

    public static void main(String[] a) throws IOException, InterruptedException, ExecutionException {

        ExecutorService service = Executors.newFixedThreadPool(10000);

        ArrayList<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            tasks.add(createCallable(i, 1));
        }
        service.invokeAll(tasks);
        printHighScores(1);

        service.shutdown();
    }

    private static Callable<String> createCallable(int points, int userid) {
        return new Callable<String>() {
                @Override
                public String call() throws Exception {
                    System.out.println(Thread.currentThread().getName());
                    long n1 = System.nanoTime();
                    String sessionId = login(userid);
                    Thread.sleep(100);
                    setScore(sessionId, points, userid);
                    Thread.sleep(100);
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
