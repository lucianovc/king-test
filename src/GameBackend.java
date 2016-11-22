import java.util.*;
import java.util.concurrent.*;

/**
 * Created by luciano on 8/11/16.
 */
public class GameBackend {

    private Map<String, Integer> sessions;
    private Map<Integer, Level> levels;
    private NavigableMap<Long, Set<String>> timestamp;
    private ScheduledExecutorService sch;

    public GameBackend() {
        this.sessions = new ConcurrentHashMap<>();
        this.levels = new ConcurrentHashMap<>();
        this.timestamp = new ConcurrentSkipListMap<>();
        sch = Executors.newSingleThreadScheduledExecutor();
        sch.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long TIME = 10L * 60000L;
                int num = removeLessThan(System.currentTimeMillis() - TIME);
                System.out.println(String.format("removed %d entries", num));
            }
        }, 10L, 10L, TimeUnit.MINUTES);
    }

    public int removeLessThan(long tstamp) {
        System.out.println(timestamp.size() + "@#");
        Iterator<Long> it = timestamp.keySet().iterator();
        int i = 0;
        while (it.hasNext()) {
            Long t = it.next();
            if (t < tstamp) {
                Set<String> sessions = timestamp.get(t);
                for (String s :
                        sessions) {
                    this.sessions.remove(s);
                }
                timestamp.remove(t);
            } else {
                return i;
            }
            i++;
        }
        return i;
    }

    public String createSessionId(Integer userId) {
        String sessionid = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE) + "";
        sessions.put(sessionid, userId);
        long key = System.nanoTime();
        if (!timestamp.containsKey(key)) {
            timestamp.put(key, new ConcurrentSkipListSet<>());
        }
        timestamp.get(key).add(sessionid);

        return sessionid;
    }

    public String getHighscores(Integer level) {
        String response;
        Level l = levels.get(level);
        if (l == null) {
            response = "";
        } else {
            response = printHighscores(l);
        }
        return response;
    }

    private String printHighscores(Level l) {
        Iterator<Integer> it = l.getScores().descendingIterator();
        System.out.println(l.getScores().size() + " scores");
        int i = 0;
        StringBuilder buff = new StringBuilder(15);
        while (it.hasNext() && i < 15) {
            Integer score = it.next();
            Set<Integer> users = l.getScoreUsers().get(score);
            for (Integer u : users) {

                if (i > 0) {
                    buff.append(",");
                }
                buff.append(u);
                buff.append("=");
                buff.append(score);
                i++;
            }
        }
        return buff.toString();
    }

    public boolean setUserScoreInLevel(Integer level, String session, int score) {
        Integer userId = sessions.get(session);
        if (userId == null) {
            return false;
        }
        Level l = getLevel(level);
        l.setScore(score, userId);
        return true;
    }

    private Level getLevel(Integer level) {
        Level l = null;
        if (!levels.containsKey(level)) {
            l = new Level();
            l.setLevel(level);
            levels.put(level, l);
        }
        l = levels.get(level);
        return l;
    }

    public String getDump() {
        String response;
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> v : sessions.entrySet()) {
            sb.append(v.getKey()).append("=").append(v.getValue()).append(", ");
        }

        for (Map.Entry<Integer, Level> v : levels.entrySet()) {
            sb.append("##");
            sb.append("Level ").append(v.getKey()).append(":");
            Level level = v.getValue();
            NavigableSet<Integer> scores = level.getScores();
            for (Integer s : scores) {
                Map<Integer, Set<Integer>> scoreUsers = level.getScoreUsers();
                Set<Integer> users = scoreUsers.get(s);
                for (Integer u : users) {
                    sb.append(u).append("=").append(s).append(", ");
                }
            }
        }

        response = sb.toString();
        return response;
    }

}
