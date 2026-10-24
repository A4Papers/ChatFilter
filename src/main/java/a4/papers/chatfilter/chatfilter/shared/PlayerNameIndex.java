package a4.papers.chatfilter.chatfilter.shared;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PlayerNameIndex {
    private final Map<UUID, Name> names = new LinkedHashMap<>();
    private volatile Snapshot snapshot = new Snapshot(List.of(), new LiteralMatcher(List.of()));

    public synchronized void join(UUID player, String name) {
        names.put(player, new Name(name, name.toLowerCase()));
        rebuild();
    }

    public synchronized void quit(UUID player) {
        if (names.remove(player) != null) {
            rebuild();
        }
    }

    public synchronized void clear() {
        names.clear();
        rebuild();
    }

    private void rebuild() {
        List<Name> entries = List.copyOf(names.values());
        List<String> lowercase = new ArrayList<>(entries.size());
        for (Name entry : entries) {
            lowercase.add(entry.lowercase());
        }
        snapshot = new Snapshot(entries, new LiteralMatcher(lowercase));
    }

    public String restore(String message) {
        Snapshot current = snapshot;
        String lowercase = message.toLowerCase();
        if (!current.matcher().containsAny(lowercase)) {
            return message;
        }
        String restored = message;
        for (Name name : current.names()) {
            if (lowercase.contains(name.lowercase())) {
                restored = restored.replace(name.lowercase(), name.original());
            }
        }
        return restored;
    }

    private record Name(String original, String lowercase) {
    }

    private record Snapshot(List<Name> names, LiteralMatcher matcher) {
    }
}
