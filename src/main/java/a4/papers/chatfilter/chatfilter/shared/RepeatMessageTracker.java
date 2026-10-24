package a4.papers.chatfilter.chatfilter.shared;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class RepeatMessageTracker {
    private final Map<UUID, Session> sessions = new ConcurrentHashMap<>();

    public void join(UUID player) {
        sessions.put(player, new Session());
    }

    public void quit(UUID player) {
        sessions.remove(player);
    }

    public void clear() {
        sessions.clear();
    }

    public long record(UUID player, String message, long now, long delay, double threshold) {
        Session session = sessions.get(player);
        return session == null ? 0 : session.record(message, now, delay, threshold);
    }

    private static final class Session {
        private String message;
        private long expires;

        private synchronized long record(String next, long now, long delay, double threshold) {
            if (message != null && expires > now
                    && StringSimilarity.exceedsSimilarity(next, message, threshold)) {
                return expires - now;
            }
            message = next;
            expires = now + delay;
            return 0;
        }
    }
}
