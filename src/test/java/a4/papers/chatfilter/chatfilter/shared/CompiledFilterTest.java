package a4.papers.chatfilter.chatfilter.shared;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import static org.junit.jupiter.api.Assertions.*;

class CompiledFilterTest {
    @Test
    void rejectsMissingRequiredCharactersAndRetainsRegexFallback() {
        CompiledFilter filter = new CompiledFilter(Pattern.compile("((a|@)+(\\W|_)*(b|8)+(\\W|_)*)"), null);
        assertFalse(mightMatch(filter, "hello"));
        assertTrue(mightMatch(filter, "@8"));
        for (String pattern : List.of("a|b", "(a)?", "(?i)abc", "[a-z]+", "(\\p{L})+", "()", "\\b(a)+.*")) {
            assertTrue(new CompiledFilter(Pattern.compile(pattern), null).mightMatch(0, 0));
        }
        assertTrue(new CompiledFilter(Pattern.compile("((a)+(\\W|_)*)", Pattern.CASE_INSENSITIVE), null).mightMatch(0, 0));
    }

    @Test
    void neverRejectsMatchesFromTheBundledFilters() throws Exception {
        List<CompiledFilter> filters = new ArrayList<>();
        for (String resource : List.of("wordFilters.yml", "advertFilters.yml")) {
            try (var stream = getClass().getResourceAsStream("/" + resource);
                 var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(reader);
                var section = config.getConfigurationSection("ChatFilter");
                for (String key : section.getKeys(false)) {
                    String path = key + ".Regex";
                    List<String> patterns = section.getStringList(path);
                    if (patterns.isEmpty()) patterns = List.of(section.getString(path));
                    for (String pattern : patterns) filters.add(new CompiledFilter(Pattern.compile(pattern), null));
                }
            }
        }
        Random random = new Random(22);
        String alphabet = "abcdefghijklmnopqrstuvwxyza@!$418730_ (.-\u03b1\uD83D\uDE00";
        List<String> inputs = new ArrayList<>(List.of("test@example.com", "1.2.3.4", "hello everyone", "bad words",
                "sh!t", "f_u_c_k", "fu((k", "sex", "s3x", "b00b", "ASSHOLE", "hentai", "@r0u$3d"));
        for (int i = 0; i < 1000; i++) {
            StringBuilder text = new StringBuilder();
            for (int size = random.nextInt(40); size > 0; size--) text.append(alphabet.charAt(random.nextInt(alphabet.length())));
            inputs.add(text.toString());
        }
        for (CompiledFilter filter : filters) {
            for (String input : inputs) {
                if (filter.getPattern().matcher(input).find()) {
                    assertTrue(mightMatch(filter, input), () -> filter.getPattern() + " rejected " + input);
                }
            }
        }
    }

    private static boolean mightMatch(CompiledFilter filter, String text) {
        long low = 0;
        long high = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c < 64) low |= 1L << c;
            else if (c < 128) high |= 1L << (c - 64);
        }
        return filter.mightMatch(low, high);
    }
}
