import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by luciano on 3/11/16.
 */
public class Level {

    private int level;
    private NavigableSet<Integer> scores;
    private Map<Integer, Set<Integer>> scoreUsers;

    public Level() {
        this.scores = new ConcurrentSkipListSet<>();
        setScoreUsers(new ConcurrentHashMap<>());
    }

    public Level(int level, int score, int user) {
        this();
        this.level = level;
        scores.add(score);
        getScoreUsers().putIfAbsent(score, new ConcurrentSkipListSet<>());
        Set<Integer> users = getScoreUsers().get(score);
        users.add(user);
    }

    public void setScore(int score, int user) {
        scores.add(score);
        if (!getScoreUsers().containsKey(score)){
            getScoreUsers().put(score, new ConcurrentSkipListSet<>());
        }
        Set<Integer> users = this.getScoreUsers().get(score);
        users.add(user);
    }

    public NavigableSet<Integer> getScores() {
        return scores;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public static void main(String[] a) {

    }

    public Map<Integer, Set<Integer>> getScoreUsers() {
        return scoreUsers;
    }

    public void setScoreUsers(Map<Integer, Set<Integer>> scoreUsers) {
        this.scoreUsers = scoreUsers;
    }
}
