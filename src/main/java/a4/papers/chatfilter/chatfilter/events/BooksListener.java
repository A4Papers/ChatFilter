package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Result;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if (event.getPlayer().hasPermission("chatfilter.bypass") || event.getPlayer().hasPermission("chatfilter.bypass.book"))
            return;
        List<String> catchMatch = new ArrayList<>();
        List<String> bookPageMatch = new ArrayList<>();
        List<String> bookPagesList = event.getNewBookMeta().getPages();
        String prefix = "Error";
        String warnPlayerMessage = "Error";
        FilterWrapper filterWrapper = null;
        Types type = Types.NOTYPE;
        boolean resulted = false;
        int nom = 0;
        String[] stringArray;
        for (String pageFilter : bookPagesList) {
            Result result = chatFilter.getChatFilters().validResult(pageFilter, p);
            type = result.getType();
            stringArray = result.getStringArray();
            filterWrapper = result.getFilterWrapper();
            chatFilter.commandHandler.runCommand(p, stringArray, filterWrapper);
            nom = bookPagesList.indexOf(pageFilter) + 1;
            if (result.getResult()) {
                resulted = true;
                switch (type) {
                    case SWEAR:
                        bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", nom+"") + pageFilter);
                        warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                        prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookSwear.s).replace("%player%", p.getName());
                        break;
                    case IP_DNS:
                        bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", nom+"") + pageFilter);
                        warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                        prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookIP.s).replace("%player%", p.getName());
                        break;
                    case IP_SWEAR:
                        bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", nom+"") + pageFilter);
                        warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                        prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookIPandSwear.s).replace("%player%", p.getName());
                        break;
                    case FONT:
                        bookPageMatch.add(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.bookPage.s)).replace("%num%", nom+"") + pageFilter);
                        warnPlayerMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s));
                        prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookFont.s).replace("%player%", p.getName());
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
                catchMatch.addAll(Arrays.asList(stringArray));
            }
        }
        if (resulted) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WRITABLE_BOOK)) {
                event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.WRITABLE_BOOK, 1));
                    }
                }.runTaskLater(chatFilter, 1);
            }
            if (filterWrapper.getLogToConsole())
                chatFilter.sendConsole(type, bookPagesList.get(nom - 1), p, filterWrapper.getRegex(), "Book");
            if (filterWrapper.getWarnPlayer()) {
                p.sendMessage(chatFilter.colour(warnPlayerMessage));
            }
            if (filterWrapper.getSendStaff()) {
                chatFilter.sendStaffMessage(chatFilter.colour(prefix));
                for (String page : bookPageMatch) {
                    for (String oneWord : catchMatch) {
                        page = page.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%", oneWord)));
                    }
                    chatFilter.sendStaffMessage(page);
                }
            }
            bookPageMatch.clear();
            catchMatch.clear();
        }
    }
}
