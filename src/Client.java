import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by luciano on 1/11/16.
 */
public class Client {

    public static void main(String[] a) throws IOException, InterruptedException {

        for (int i=0; i< 1000; i++){

            String sessionId = login(1);
            String sessionId2 = login(2);
            String sessionId3 = login(3);

            setScore(sessionId, 105, 1);
            setScore(sessionId, 110, 1);
            setScore(sessionId2, 106, 1);
            setScore(sessionId3, 107, 1);
            setScore(sessionId2, 111, 1);
            setScore(sessionId, 100, 1);
            printHighScores(1);

//            Thread.sleep(1000);
//
//            dump();
        }

    }

    private static void setScore(String sessionId, int score, int level) throws IOException {
        URL url = new URL("http://localhost:8081/" + level + "/score?sessionkey=" + sessionId);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.getOutputStream().write(String.valueOf(score).getBytes());
        int res = con.getResponseCode();
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

        BufferedReader reader = toBufferedStream(con);
        String response = reader.readLine();
        response = response == null ? "" : response;
        System.out.println(response + "#");
        con.disconnect();
        reader.close();
    }
}
