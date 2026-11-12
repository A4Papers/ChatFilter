package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.commands.CommandHandler;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommandSchedulingTest {
    @Test
    void manyPunishmentsUseOneGlobalTaskAndKeepActionOrder() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            ChatFilter plugin = mock(ChatFilter.class);
            when(plugin.tasks()).thenReturn(fixture.scheduler);
            when(plugin.colour(anyString())).thenAnswer(call -> call.getArgument(0));
            Player player = mock(Player.class);
            when(player.getName()).thenReturn("Tester");
            ConsoleCommandSender console = mock(ConsoleCommandSender.class);
            when(fixture.server.getConsoleSender()).thenReturn(console);
            CommandHandler handler = new CommandHandler(plugin);
            handler.runCommand(player, new String[]{"word"}, wrapper(List.of(
                    "<RunCommand>warn %player% %item%", "<SendMessage>caught %item%",
                    "<RunCommand>mute %player%", "none", "<RunCommand>log %item%")));
            assertEquals(1, fixture.globalQueue.size());
            verify(player).sendMessage("caught word");
            fixture.globalQueue.remove().run();
            var order = inOrder(fixture.server);
            order.verify(fixture.server).dispatchCommand(console, "warn Tester word");
            order.verify(fixture.server).dispatchCommand(console, "mute Tester");
            order.verify(fixture.server).dispatchCommand(console, "log word");
        }
    }

    @Test
    void messageOnlyAndEmptyActionsScheduleNothing() {
        try (SchedulerFixture fixture = new SchedulerFixture()) {
            ChatFilter plugin = mock(ChatFilter.class);
            when(plugin.tasks()).thenReturn(fixture.scheduler);
            when(plugin.colour(anyString())).thenAnswer(call -> call.getArgument(0));
            Player player = mock(Player.class);
            when(player.getName()).thenReturn("Tester");
            CommandHandler handler = new CommandHandler(plugin);
            handler.runCommand(player, new String[]{"word"}, wrapper(List.of("none", "<SendMessage>message")));
            handler.runCommand(player, new String[0], wrapper(List.of("<RunCommand>unused")));
            assertTrue(fixture.globalQueue.isEmpty());
            assertTrue(fixture.asyncQueue.isEmpty());
            assertTrue(fixture.entityQueue.isEmpty());
            verify(player).sendMessage("message");
        }
    }

    private static FilterWrapper wrapper(List<String> actions) {
        return new FilterWrapper("word", actions, "word", false, false, "", false, false, false);
    }
}
