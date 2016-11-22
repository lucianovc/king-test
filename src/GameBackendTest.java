import org.junit.Assert;
import org.junit.Test;

/**
 * Created by luciano on 8/11/16.
 */
public class GameBackendTest {

    GameBackend backend = new GameBackend();

    @Test
    public void testBasic() throws InterruptedException {

        String hs = backend.getHighscores(1);
        Assert.assertEquals("", hs);

        String login1 = backend.createSessionId(1);
        String login2 = backend.createSessionId(2);
        String login3 = backend.createSessionId(3);
        String login4 = backend.createSessionId(4);
        String login5 = backend.createSessionId(5);
        String login6 = backend.createSessionId(6);
        String login7 = backend.createSessionId(7);
        String login8 = backend.createSessionId(8);

        Long t = System.nanoTime();
        int i = backend.removeLessThan(t);
        Assert.assertEquals(8, i);

        String login9 = backend.createSessionId(9);
        String login10 = backend.createSessionId(10);
        String login11 = backend.createSessionId(11);
        String login12 = backend.createSessionId(12);
        String login13 = backend.createSessionId(13);
        String login14 = backend.createSessionId(14);
        String login15 = backend.createSessionId(15);
        String login16 = backend.createSessionId(16);

        backend.setUserScoreInLevel(1, login1, 10);
        backend.setUserScoreInLevel(1, login2, 20);
        backend.setUserScoreInLevel(2, login1, 999);
        backend.setUserScoreInLevel(1, login3, 100);
        backend.setUserScoreInLevel(1, login4, 200);
        backend.setUserScoreInLevel(1, login5, 101);
        backend.setUserScoreInLevel(1, login6, 11);
        backend.setUserScoreInLevel(1, login7, 12);
        backend.setUserScoreInLevel(1, login8, 13);
        backend.setUserScoreInLevel(1, login9, 14);
        backend.setUserScoreInLevel(1, login10, 15);
        backend.setUserScoreInLevel(1, login11, 16);
        backend.setUserScoreInLevel(1, login12, 17);
        backend.setUserScoreInLevel(1, login13, 18);
        backend.setUserScoreInLevel(1, login14, 19);
        backend.setUserScoreInLevel(1, login15, 9);
        backend.setUserScoreInLevel(1, login16, 1000);
        backend.setUserScoreInLevel(1, login15, 99);

        hs = backend.getHighscores(1);
        Assert.assertEquals("16=1000,15=99,14=19,13=18,12=17,11=16,10=15,9=14,15=9", hs);

        Long t2 = System.nanoTime();
        i = backend.removeLessThan(t);
        Assert.assertEquals(0, i);
        i = backend.removeLessThan(t2);
        Assert.assertEquals(8, i);

    }

}
