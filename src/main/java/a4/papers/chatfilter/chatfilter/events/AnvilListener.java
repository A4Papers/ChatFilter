package a4.papers.chatfilter.chatfilter.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.EventExecutor;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.FilterWrapper;
import a4.papers.chatfilter.chatfilter.shared.Result;
import a4.papers.chatfilter.chatfilter.shared.Types;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;

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
        if (event.isCancelled()) {
            return;
        }
        Inventory inv = event.getInventory();
        if (!(inv instanceof AnvilInventory)) {
            return;
        }
        InventoryView view = event.getView();
        int rawSlot = event.getRawSlot();
        if (rawSlot != view.convertSlot(rawSlot) || rawSlot != 2) {
            return;
        }
        ItemStack item = event.getCurrentItem();
        if (item == null) {
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }
        HumanEntity ent = event.getWhoClicked();
        if (!(ent instanceof Player)) {
            return;
        }
        Player p = (Player) ent;
        if (p.isOp() || p.hasPermission("chatfilter.bypass") || p.hasPermission("chatfilter.bypass.anvil"))
            return;
        String displayName = ChatColor.stripColor(meta.getDisplayName());
        Result result = chatFilter.getChatFilters().validResult(displayName, p);
        if (!result.getResult()) {
            return;
        }
        Types type = result.getType();
        String[] stringArray = result.getStringArray();
        FilterWrapper filterWrapper = result.getFilterWrapper();
        chatFilter.commandHandler.runCommand(p, stringArray, filterWrapper);
        event.setCancelled(true);
        String prefix;
        String warnPlayerMessage;
        switch (type) {
            case SWEAR:
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilSwear.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                break;
            case IP_DNS:
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilIP.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                break;
            case IP_SWEAR:
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilIPandSwear.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnSwearAndIPMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                break;
            case FONT:
                prefix = chatFilter.getLang().mapToString(EnumStrings.prefixAnvilFont.s).replace("%player%", p.getName());
                warnPlayerMessage = chatFilter.getLang().mapToString(EnumStrings.warnFontMessage.s).replace("%placeHolder%", (chatFilter.getLang().stringArrayToString(stringArray)));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        if (filterWrapper.getWarnPlayer())
            p.sendMessage(chatFilter.colour(warnPlayerMessage));
        if (filterWrapper.getLogToConsole())
            chatFilter.sendConsole(type, displayName, p, filterWrapper.getRegex(), "Anvil");
        if (filterWrapper.getSendStaff()) {
            String highlightColored = chatFilter.colour(chatFilter.settingsSwearHighLight);
            for (int i = 0; i < stringArray.length; i++) {
                displayName = displayName.replace(stringArray[i], highlightColored.replace("%catch%", stringArray[i]));
            }
            chatFilter.sendStaffMessage(chatFilter.colour(prefix + displayName));
        }
    }
}
