package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.lang.Types;
import a4.papers.chatfilter.chatfilter.lang.enumStrings;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AnvilListener implements Listener {

    ChatFilter chatFilter;

    public AnvilListener(ChatFilter instance) {
        chatFilter = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasPermission("chatfilter.bypass") || event.getWhoClicked().hasPermission("chatfilter.bypass.anvil")) {
            return;
        }
        if (!event.isCancelled()) {
            HumanEntity ent = event.getWhoClicked();
            if (ent instanceof Player) {
                Player p = (Player) ent;
                Inventory inv = event.getInventory();
                if (inv instanceof AnvilInventory) {
                    InventoryView view = event.getView();
                    int rawSlot = event.getRawSlot();
                    if (rawSlot == view.convertSlot(rawSlot)) {
                        if (rawSlot == 2) {
                            ItemStack item = event.getCurrentItem();
                            if (item != null) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta != null) {
                                    if (meta.hasDisplayName()) {
                                        String displayName = ChatColor.stripColor(meta.getDisplayName());
                                        String prefix = "";
                                        String warnPlayerMessage = "";
                                        if (chatFilter.getChatFilters().validResult(displayName, p).getResult()) {
                                            Types type = chatFilter.getChatFilters().validResult(displayName, p).getType();
                                            chatFilter.commandHandler.runCommand(type, p, chatFilter.getChatFilters().validResult(displayName, p).getStringArray());
                                            event.setCancelled(true);
                                            if (type == Types.SWEAR) {
                                                prefix = chatFilter.colour(chatFilter.mapToString(enumStrings.prefixAnvilSwear.s).replace("%player%", p.getName()));
                                                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(enumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(displayName, p).getStringArray()))));
                                            }
                                            if (type == Types.IP_DNS) {
                                                prefix = chatFilter.colour(chatFilter.mapToString(enumStrings.prefixAnvilIP.s).replace("%player%", p.getName()));
                                                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(enumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(displayName, p).getStringArray()))));
                                            }
                                            if (type == Types.IP_SWEAR) {
                                                prefix = chatFilter.colour(chatFilter.mapToString(enumStrings.prefixAnvilIPandSwear.s).replace("%player%", p.getName()));
                                                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(enumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(displayName, p).getStringArray()))));
                                            }
                                            if (type == Types.FONT) {
                                                prefix = chatFilter.colour(chatFilter.mapToString(enumStrings.prefixAnvilFont.s).replace("%player%", p.getName()));
                                                warnPlayerMessage = chatFilter.colour(chatFilter.mapToString(enumStrings.warnFontMessage.s).replace("%placeHolder%", (chatFilter.stringArrayToString(chatFilter.getChatFilters().validResult(displayName, p).getStringArray()))));
                                            }
                                            chatFilter.sendConsole(chatFilter.getChatFilters().validResult(displayName, p).getType(), displayName, p, chatFilter.getChatFilters().validResult(displayName, p).getRegexPattern(), "Advil");
                                            p.sendMessage(warnPlayerMessage);
                                            for (String oneWord : chatFilter.getChatFilters().validResult(displayName, p).getStringArray()) {
                                                displayName = displayName.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight + oneWord + ChatColor.WHITE));
                                            }
                                            chatFilter.sendStaffMessage(prefix + displayName);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}