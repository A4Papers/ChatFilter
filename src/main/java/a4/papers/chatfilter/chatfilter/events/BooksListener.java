package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class BooksListener implements Listener {
    ChatFilter chatFilter;

    public BooksListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBookEvent(PlayerEditBookEvent event) {
        Player p = event.getPlayer();
        if (event.getPlayer().hasPermission("chatfilter.bypass") || event.getPlayer().hasPermission("chatfilter.bypass.book"))
            return;
        List<String> BookPageMatch = new ArrayList<>();
        List<String> bookPagesList = event.getNewBookMeta().getPages();
        String prefix = "Error";
        String warnPlayerMessage = "Error";
        boolean result = false;
        for (String pageFilter : bookPagesList) {
            int nom = bookPagesList.indexOf(pageFilter) + 1;
            String[] stringArray = chatFilter.getChatFilters().validResult(pageFilter, p).getStringArray();
            FilterWrapper filterWrapper = chatFilter.getChatFilters().validResult(pageFilter, p).getFilterWrapper();

            if (chatFilter.getChatFilters().validResult(pageFilter, p).getResult()) {
                Types type = chatFilter.getChatFilters().validResult(pageFilter, p).getType();

                result = true;
                if (type == Types.SWEAR) {
                    if (!BookPageMatch.contains(pageFilter)) {
                        BookPageMatch.add(ChatColor.GOLD + "Page" + nom + ChatColor.WHITE + ": " + pageFilter);
                    }
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookSwear.s).replace("%player%", p.getName());
                }
                if (type == Types.IP_DNS) {
                    if (!BookPageMatch.contains(pageFilter)) {
                        BookPageMatch.add(ChatColor.GOLD + "Page" + nom + ChatColor.WHITE + ": " + pageFilter);
                    }
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookIP.s).replace("%player%", p.getName());
                }
                if (type == Types.IP_SWEAR) {
                    if (!BookPageMatch.contains(pageFilter)) {
                        BookPageMatch.add(ChatColor.GOLD + "Page" + nom + ChatColor.WHITE + ": " + pageFilter);
                    }
                    warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookIPandSwear.s).replace("%player%", p.getName());
                }
                if (type == Types.FONT) {
                    if (!BookPageMatch.contains(pageFilter)) {
                        BookPageMatch.add(ChatColor.GOLD + "Page" + nom + ChatColor.WHITE + ": " + pageFilter);
                    }
                    warnPlayerMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s));
                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixBookFont.s).replace("%player%", p.getName());
                }
            }

            if (result) {
                if (event.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.WRITABLE_BOOK)) {
                    event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getInventory().getItemInMainHand().getAmount() - 1);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.WRITABLE_BOOK, 1));
                        }
                    }.runTaskLater(chatFilter, 1);
                }
                if(filterWrapper.getWarnPlayer()) {
                    p.sendMessage(chatFilter.colour(warnPlayerMessage));
                }
                if (filterWrapper.getSendStaff()) {
                    chatFilter.sendStaffMessage(chatFilter.colour(prefix));

                    for (String oneWord : BookPageMatch) {
                        chatFilter.sendStaffMessage(oneWord);
                    }
                    BookPageMatch.clear();
                }
            }
        }
    }
}
