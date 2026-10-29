package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.shared.LowerCaseReplace;
import a4.papers.chatfilter.chatfilter.shared.lang.LangManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ResourceLifecycleTest {
    @Test
    void metricsExecutorsAreOwnedAndStoppedIndependently() throws Exception {
        Metrics.MetricsBase first = metrics(true);
        Metrics.MetricsBase second = metrics(true);
        Metrics.MetricsBase disabled = metrics(false);
        Field field = Metrics.MetricsBase.class.getDeclaredField("scheduler");
        field.setAccessible(true);
        ScheduledThreadPoolExecutor firstExecutor = (ScheduledThreadPoolExecutor) field.get(first);
        ScheduledThreadPoolExecutor secondExecutor = (ScheduledThreadPoolExecutor) field.get(second);
        try {
            assertEquals(2, firstExecutor.getQueue().size());
            assertNull(field.get(disabled));
            first.shutdown();
            assertTrue(firstExecutor.isShutdown());
            assertTrue(firstExecutor.getQueue().isEmpty());
            assertFalse(secondExecutor.isShutdown());
        } finally {
            first.shutdown();
            second.shutdown();
            disabled.shutdown();
        }
    }

    @Test
    void languageReloadReadsUpdatedFiles(@TempDir Path directory) throws Exception {
        ChatFilter plugin = mock(ChatFilter.class);
        YamlConfiguration config = new YamlConfiguration();
        config.set("locale", "en");
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getDataFolder()).thenReturn(directory.toFile());
        Path language = directory.resolve("messages_en.properties");
        Files.writeString(language, "test=before\n");
        LangManager manager = new LangManager(plugin);
        manager.loadLang();
        assertEquals("before", manager.mapToString("test"));
        Files.writeString(language, "test=after\n");
        manager.loadLang();
        assertEquals("after", manager.mapToString("test"));
        Files.delete(language);
    }

    @Test
    void caseInsensitiveReplacementHandlesEmptyAndMissingTargets() {
        assertEquals("text", LowerCaseReplace.replace("text", "", "x"));
        assertEquals("text", LowerCaseReplace.replace("text", "missing", "x"));
        assertEquals("x x", LowerCaseReplace.replace("BAD bad", "bad", "x"));
    }

    @Test
    void savesOnlyTheChangedConfiguration(@TempDir Path directory) throws Exception {
        ChatFilter plugin = mock(ChatFilter.class);
        for (String name : java.util.List.of("whitelist", "word", "advert", "unicode")) {
            YamlConfiguration config = new YamlConfiguration();
            config.set("test", name);
            Field configuration = ChatFilter.class.getDeclaredField(name + "Config");
            configuration.setAccessible(true);
            configuration.set(plugin, config);
            Field file = ChatFilter.class.getDeclaredField(name + "ConfigFile");
            file.setAccessible(true);
            file.set(plugin, directory.resolve(name + ".yml").toFile());
        }
        doCallRealMethod().when(plugin).saveWhitelist();
        doCallRealMethod().when(plugin).saveWordFilters();
        doCallRealMethod().when(plugin).saveAdvertFilters();
        plugin.saveWhitelist();
        assertTrue(Files.exists(directory.resolve("whitelist.yml")));
        assertFalse(Files.exists(directory.resolve("word.yml")));
        assertFalse(Files.exists(directory.resolve("advert.yml")));
        assertFalse(Files.exists(directory.resolve("unicode.yml")));
        plugin.saveWordFilters();
        plugin.saveAdvertFilters();
        assertTrue(Files.exists(directory.resolve("word.yml")));
        assertTrue(Files.exists(directory.resolve("advert.yml")));
        assertFalse(Files.exists(directory.resolve("unicode.yml")));
    }

    private static Metrics.MetricsBase metrics(boolean enabled) {
        return new Metrics.MetricsBase("bukkit", "test", 1, enabled, builder -> {}, builder -> {},
                Runnable::run, () -> true, (message, error) -> {}, message -> {}, false, false, false);
    }
}
