package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.commands.ImportCommand;
import a4.papers.chatfilter.chatfilter.shared.regexHandler.LoadFilters;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImportSchedulingTest {
    @Test
    void readsAsynchronouslyAppliesGloballyAndRepliesOnPlayerScheduler(@TempDir Path directory) throws Exception {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            ChatFilter plugin = plugin(fixture, directory);
            Player player = player(fixture);
            LoadFilters filters = mock(LoadFilters.class);
            when(plugin.getFilters()).thenReturn(filters);
            Files.writeString(directory.resolve("data.txt"), " First \n\nX\nSECOND\n");
            ImportCommand command = new ImportCommand(plugin);
            command.load(player);
            assertEquals(1, fixture.asyncQueue.size());
            assertTrue(fixture.globalQueue.isEmpty());
            verifyNoInteractions(filters);
            fixture.asyncQueue.remove().run();
            assertEquals(1, fixture.globalQueue.size());
            verifyNoInteractions(filters);
            fixture.globalQueue.remove().run();
            verify(filters).importWordFilters(List.of("first", "second"), "Imported by Tester");
            verify(player, never()).sendMessage(anyString());
            assertEquals(1, fixture.entityQueue.size());
            fixture.entityQueue.remove().run();
            verify(player).sendMessage(contains("Successfully imported 2 words"));
        }
    }

    @Test
    void overlappingImportsDoNotDuplicateAsyncOrGlobalWork(@TempDir Path directory) throws Exception {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            ChatFilter plugin = plugin(fixture, directory);
            when(plugin.getFilters()).thenReturn(mock(LoadFilters.class));
            Player player = player(fixture);
            Files.writeString(directory.resolve("data.txt"), "first\n");
            ImportCommand command = new ImportCommand(plugin);
            command.load(player);
            command.load(player);
            assertEquals(1, fixture.asyncQueue.size());
            fixture.asyncQueue.remove().run();
            command.load(player);
            assertEquals(1, fixture.globalQueue.size());
            assertTrue(fixture.asyncQueue.isEmpty());
            fixture.globalQueue.remove().run();
            command.load(player);
            assertEquals(1, fixture.asyncQueue.size());
            verify(player, times(2)).sendMessage(contains("already running"));
        }
    }

    @Test
    void readFailuresReplyOnPlayerThreadAndPermitRetry(@TempDir Path directory) throws Exception {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            ChatFilter plugin = plugin(fixture, directory);
            Player player = player(fixture);
            ImportCommand command = new ImportCommand(plugin);
            command.load(player);
            fixture.asyncQueue.remove().run();
            assertTrue(fixture.globalQueue.isEmpty());
            verify(player, never()).sendMessage(anyString());
            fixture.entityQueue.remove().run();
            verify(player).sendMessage(contains("data.txt file is not found"));
            command.load(player);
            assertEquals(1, fixture.asyncQueue.size());
        }
    }

    @Test
    void applyFailuresReleaseImportGate(@TempDir Path directory) throws Exception {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            ChatFilter plugin = plugin(fixture, directory);
            Player player = player(fixture);
            LoadFilters filters = mock(LoadFilters.class);
            when(plugin.getFilters()).thenReturn(filters);
            doThrow(new IllegalStateException("invalid pattern")).when(filters).importWordFilters(anyList(), anyString());
            Files.writeString(directory.resolve("data.txt"), "first\n");
            ImportCommand command = new ImportCommand(plugin);
            command.load(player);
            fixture.asyncQueue.remove().run();
            fixture.globalQueue.remove().run();
            fixture.entityQueue.remove().run();
            verify(player).sendMessage(contains("Error importing data.txt: invalid pattern"));
            command.load(player);
            assertEquals(1, fixture.asyncQueue.size());
        }
    }

    @Test
    void shutdownBetweenReadAndApplyPreventsConfigMutation(@TempDir Path directory) throws Exception {
        SchedulerFixture fixture = new SchedulerFixture();
        ChatFilter plugin = plugin(fixture, directory);
        LoadFilters filters = mock(LoadFilters.class);
        when(plugin.getFilters()).thenReturn(filters);
        Files.writeString(directory.resolve("data.txt"), "first\n");
        new ImportCommand(plugin).load(player(fixture));
        fixture.asyncQueue.remove().run();
        fixture.close();
        fixture.globalQueue.remove().run();
        verifyNoInteractions(filters);
        assertTrue(fixture.entityQueue.isEmpty());
    }

    private static ChatFilter plugin(SchedulerFixture fixture, Path directory) throws Exception {
        ChatFilter plugin = mock(ChatFilter.class);
        when(plugin.tasks()).thenReturn(fixture.scheduler);
        when(plugin.getDataFolder()).thenReturn(directory.toFile());
        return plugin;
    }

    private static Player player(SchedulerFixture fixture) {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn("Tester");
        when(player.getScheduler()).thenReturn(fixture.entity);
        return player;
    }
}
