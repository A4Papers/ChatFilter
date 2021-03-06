package a4.papers.chatfilter.chatfilter.events;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Result;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.EventExecutor;

public class AnvilListener implements EventExecutor, Listener {

    ChatFilter chatFilter;

    public AnvilListener(ChatFilter instance) {
        chatFilter = instance;
    }
    @Override
    public void execute(final Listener listener, final Event event) throws EventException {
        this.onInventoryClick((InventoryClickEvent) event);
    }

    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked().hasPermission("chatfilter.bypass") || event.getWhoClicked().hasPermission("chatfilter.bypass.anvil"))
            return;
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
                                        Result result = chatFilter.getChatFilters().validResult(displayName, p);
                                        if (result.getResult()) {
                                            Types type = result.getType();
                                            String[] stringArray = result.getStringArray();
                                            FilterWrapper filterWrapper = result.getFilterWrapper();
                                            chatFilter.commandHandler.runCommand(p, stringArray, filterWrapper);
                                            event.setCancelled(true);
                                            switch (type) {
                                                case SWEAR:
                                                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilSwear.s).replace("%player%", p.getName());
                                                    warnPlayerMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray))));
                                                    break;
                                                case IP_DNS:
                                                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilIP.s).replace("%player%", p.getName());
                                                    warnPlayerMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray))));
                                                    break;
                                                case IP_SWEAR:
                                                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilIPandSwear.s).replace("%player%", p.getName());
                                                    warnPlayerMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray))));
                                                    break;
                                                case FONT:
                                                    prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilFont.s).replace("%player%", p.getName());
                                                    warnPlayerMessage = chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray))));
                                                    break;
                                                default:
                                                    throw new IllegalStateException("Unexpected value: " + type);
                                            }
                                            if (filterWrapper.getWarnPlayer())
                                                p.sendMessage(chatFilter.colour(warnPlayerMessage));
                                            if (filterWrapper.getLogToConsole())
                                                chatFilter.sendConsole(type, displayName, p, filterWrapper.getRegex(), "Anvil");
                                            if (filterWrapper.getSendStaff()) {
                                                for (String oneWord : chatFilter.getChatFilters().validResult(displayName, p).getStringArray()) {
                                                    displayName = displayName.replace(oneWord, chatFilter.colour(chatFilter.settingsSwearHighLight.replace("%catch%", oneWord)));
                                                }
                                                chatFilter.sendStaffMessage(chatFilter.colour(prefix + displayName));
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
}
