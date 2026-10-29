package a4.papers.chatfilter.chatfilter;

import a4.papers.chatfilter.chatfilter.shared.*;
import a4.papers.chatfilter.chatfilter.shared.regexHandler.LoadFilters;
import a4.papers.chatfilter.chatfilter.shared.regexHandler.RegexpGenerator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FilterLoadingTest {
    @Test
    void fontOnlyChecksSkipTextNormalizationAndUnusedPermissions() {
        ChatFilter plugin = plugin();
        plugin.settingsBlockFancyChat = true;
        plugin.unicodeIndex = new CodePointIndex(List.of(new UnicodeWrapper("1F600", "1F64F")));
        plugin.bypassReplacements = mock(LiteralReplacements.class);
        Player player = mock(Player.class);
        ChatFilters filters = new ChatFilters(plugin);
        assertFalse(filters.validResult("CLEAN MESSAGE", player).getResult());
        Result result = filters.validResult("\uD83D\uDE00", player);
        assertEquals(Types.FONT, result.getType());
        assertEquals("unicode", result.getFilterWrapper().getRegex());
        assertSame(result, filters.validResult("\uD83D\uDE00", player));
        verifyNoInteractions(plugin.bypassReplacements);
        verify(player, never()).hasPermission(anyString());
    }

    @Test
    void compilationReusesPatternsWithoutDuplicatingPublishedLists() {
        ChatFilter plugin = plugin();
        LoadFilters loader = new LoadFilters(plugin);
        FilterWrapper word = wrapper("bad");
        plugin.regexWords.put("bad", word);
        plugin.regexAdvert.put("server", wrapper("server"));
        loader.regexCompile();
        Pattern original = plugin.wordCompiled[0].getPattern();
        loader.regexCompile();
        assertEquals(1, plugin.wordRegexPattern.size());
        assertEquals(1, plugin.advertRegexPattern.size());
        assertSame(original, plugin.wordCompiled[0].getPattern());
        FilterSnapshot snapshot = plugin.compiledFilters;
        plugin.regexWords.put("[", wrapper("invalid"));
        assertThrows(java.util.regex.PatternSyntaxException.class, loader::regexCompile);
        assertSame(snapshot, plugin.compiledFilters);
    }

    @Test
    void filteringPreservesMatchesPermissionsAndCombinedTypes() {
        ChatFilter plugin = plugin();
        LoadFilters loader = new LoadFilters(plugin);
        plugin.regexWords.put("bad", wrapper("bad"));
        plugin.regexAdvert.put("server", wrapper("server"));
        loader.regexCompile();
        ChatFilters filters = new ChatFilters(plugin);
        Player player = mock(Player.class);
        Result result = filters.validResult("BAD bad server", player);
        assertEquals(Types.IP_SWEAR, result.getType());
        assertArrayEquals(new String[]{"bad", "server"}, result.getStringArray());
        assertEquals(List.of("bad", "bad"), result.getRegexList());
        when(player.hasPermission("chatfilter.bypass.swear.bad")).thenReturn(true);
        assertEquals(Types.IP_DNS, filters.validResult("bad server", player).getType());
        Result clean = filters.validResult("hello", player);
        assertFalse(clean.getResult());
        assertSame(clean, filters.validResult("hello again", player));
        plugin.bypassReplacements = new LiteralReplacements(List.of("bad server"));
        assertFalse(filters.validResult("bad server", player).getResult());
    }

    @Test
    void unicodeWhitelistAndSupplementaryRangesStillWork() {
        ChatFilter plugin = plugin();
        plugin.settingsBlockFancyChat = true;
        plugin.unicodeIndex = new CodePointIndex(List.of(new UnicodeWrapper("1F600", "1F64F")));
        plugin.unicodeReplacements = new LiteralReplacements(List.of("allowed\uD83D\uDE00"));
        ChatFilters filters = new ChatFilters(plugin);
        assertFalse(filters.isFont("allowed\uD83D\uDE00"));
        assertTrue(filters.isFont("\uD83D\uDE00"));
        assertEquals(Types.FONT, filters.validResult("\uD83D\uDE00", mock(Player.class)).getType());
    }

    @Test
    void groupedImportsKeepExistingAndAllNewPatterns() {
        ChatFilter plugin = plugin();
        YamlConfiguration config = new YamlConfiguration();
        config.set("ChatFilter.group.Enabled", true);
        config.set("ChatFilter.group.Regex", "existing");
        when(plugin.getWordConfig()).thenReturn(config);
        when(plugin.getAdvertConfig()).thenReturn(new YamlConfiguration());
        RegexpGenerator generator = new RegexpGenerator(plugin);
        when(plugin.regexpGenerator()).thenReturn(generator);
        plugin.perWordOptionsString = "group";
        LoadFilters loader = new LoadFilters(plugin);
        loader.importWordFilters(List.of("one", "two", "three"), "test");
        verify(plugin).saveWordFilters();
        verify(plugin, never()).save();
        assertEquals(List.of("existing", generator.generateRegexp("one"), generator.generateRegexp("two"),
                generator.generateRegexp("three")), config.getStringList("ChatFilter.group.Regex"));
        assertEquals(4, plugin.wordCompiled.length);
    }

    @Test
    void reloadVariablesRefreshesSimilarityThresholdAndBypassMatcher() {
        ChatFilter plugin = plugin();
        YamlConfiguration config = new YamlConfiguration();
        config.set("settings.similarMessagePercent", "60%");
        config.set("URL_REGEX", "https?://");
        YamlConfiguration whitelist = new YamlConfiguration();
        whitelist.set("bypassWords", List.of("test"));
        when(plugin.getConfig()).thenReturn(config);
        when(plugin.getWhitelistConfig()).thenReturn(whitelist);
        doCallRealMethod().when(plugin).loadVariables();
        doCallRealMethod().when(plugin).rebuildBypassReplacements();
        plugin.loadVariables();
        assertEquals(0.6, plugin.repeatSimilarityThreshold);
        assertEquals(" ", plugin.bypassReplacements.replace("test", " "));
        config.set("settings.similarMessagePercent", "90%");
        plugin.loadVariables();
        assertEquals(0.9, plugin.repeatSimilarityThreshold);
    }

    static ChatFilter plugin() {
        ChatFilter plugin = mock(ChatFilter.class);
        plugin.regexWords = new ConcurrentHashMap<>();
        plugin.regexAdvert = new ConcurrentHashMap<>();
        plugin.unicodeBlacklist = new ConcurrentHashMap<>();
        plugin.unicodeWhitelist = new CopyOnWriteArrayList<>();
        plugin.wordCompiled = new CompiledFilter[0];
        plugin.advertCompiled = new CompiledFilter[0];
        plugin.compiledFilters = FilterSnapshot.EMPTY;
        plugin.bypassReplacements = LiteralReplacements.EMPTY;
        plugin.unicodeReplacements = LiteralReplacements.EMPTY;
        plugin.unicodeIndex = CodePointIndex.EMPTY;
        plugin.settingsAllowURL = true;
        return plugin;
    }

    private static FilterWrapper wrapper(String regex) {
        return new FilterWrapper(regex, List.of("none"), regex, true, false, "", false, false, false);
    }
}
