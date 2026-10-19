package a4.papers.chatfilter.chatfilter.events;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Result;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;

public class BooksListener implements EventExecutor, Listener {
    ChatFilter chatFilter;

    public BooksListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onBookEvent((PlayerEditBookEvent) event);
    }

    public void onBookEvent(PlayerEditBookEvent event) {
        Player p = event.getPlayer();
        if (p.isOp() || p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.book"))
            return;
        Set<String> catchMatch = new LinkedHashSet<>();
        List<String> bookPageMatch = new ArrayList<>();
        List<String> bookPagesList = event.getNewBookMeta().getPages();
        String prefix = "Error";
        String warnPlayerMessage = "Error";
        FilterWrapper filterWrapper = null;
        Types type = Types.NOTYPE;
        boolean resulted = false;
        int nom = 0;
        for (int i = 0; i < bookPagesList.size(); i++) {
            String pageFilter = bookPagesList.get(i);
            Result result = chatFilter.getChatFilters().validResult(pageFilter, p);
            if (!result.getResult()) {
                continue;
            }
            type = result.getType();
            String[] stringArray = result.getStringArray();
            filterWrapper = result.getFilterWrapper();
            chatFilter.commandHandler.runCommand(p, stringArray, filterWrapper);
            nom = i + 1;
            resulted = true;
            switch (type) {
                case SWEAR:
                    bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", String.valueOf(nom)) + pageFilter);
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookSwear.s).replace("%player%", p.getName());
                    break;
                case IP_DNS:
                    bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", String.valueOf(nom)) + pageFilter);
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookIP.s).replace("%player%", p.getName());
                    break;
                case IP_SWEAR:
                    bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", String.valueOf(nom)) + pageFilter);
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookIPandSwear.s).replace("%player%", p.getName());
                    break;
                case FONT:
                    bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", String.valueOf(nom)) + pageFilter);
                    warnPlayerMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookFont.s).replace("%player%", p.getName());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
            for (int j = 0; j < stringArray.length; j++) {
                catchMatch.add(stringArray[j]);
            }
        }
        if (resulted) {
            if (p.getInventory().getItemInMainHand().getType().equals(Material.WRITABLE_BOOK)) {
                p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);
                p.getScheduler().run(chatFilter, entity -> p.getInventory().setItemInMainHand(new ItemStack(Material.WRITABLE_BOOK, 1)), null);
            }
            if (filterWrapper.getLogToConsole())
                chatFilter.sendConsole(type, bookPagesList.get(nom - 1), p, filterWrapper.getRegex(), "Book");
            if (filterWrapper.getWarnPlayer()) {
                p.sendMessage(chatFilter.colour(warnPlayerMessage));
            }
            if (filterWrapper.getSendStaff()) {
                chatFilter.sendStaffMessage(chatFilter.colour(prefix));
                String highlightColored = chatFilter.colour(chatFilter.settingsSwearHighLight);
                for (int i = 0; i < bookPageMatch.size(); i++) {
                    String page = bookPageMatch.get(i);
                    for (String oneWord : catchMatch) {
                        page = page.replace(oneWord, highlightColored.replace("%catch%", oneWord));
                    }
                    chatFilter.sendStaffMessage(page);
                }
            }
        }
    }
}
