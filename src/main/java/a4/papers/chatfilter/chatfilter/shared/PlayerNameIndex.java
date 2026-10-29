package a4.papers.chatfilter.chatfilter.shared;

import java.util.ArrayList;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class PlayerNameIndex {
    private final Object2ObjectLinkedOpenHashMap<UUID, Name> names = new Object2ObjectLinkedOpenHashMap<>();
    private static final Snapshot EMPTY = new Snapshot(List.of(), new LiteralMatcher(List.of()));
    private volatile Snapshot snapshot = EMPTY;

    public synchronized void join(UUID player, String name) {
        Name previous = names.get(player);
        if (previous != null && previous.original().equals(name)) return;
        names.put(player, new Name(name, name.toLowerCase()));
        rebuild();
    }

    public synchronized void quit(UUID player) {
        if (names.remove(player) != null) {
            rebuild();
        }
    }

    public synchronized void replaceAll(Map<UUID, String> players) {
        names.clear();
        for (Map.Entry<UUID, String> entry : players.entrySet()) {
            String name = entry.getValue();
            names.put(entry.getKey(), new Name(name, name.toLowerCase()));
        }
        rebuild();
    }

    public synchronized void clear() {
        names.clear();
        names.trim();
        snapshot = EMPTY;
    }

    private void rebuild() {
        if (names.isEmpty()) {
            snapshot = EMPTY;
            return;
        }
        List<Name> entries = List.copyOf(names.values());
        List<String> lowercase = new ArrayList<>(entries.size());
        for (Name entry : entries) {
            lowercase.add(entry.lowercase());
        }
        snapshot = new Snapshot(entries, new LiteralMatcher(lowercase));
    }

    public String restore(String message) {
        Snapshot current = snapshot;
        if (current.names().isEmpty()) return message;
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
