package a4.papers.chatfilter.chatfilter.commands;

import a4.papers.chatfilter.chatfilter.ChatFilter;
import a4.papers.chatfilter.chatfilter.shared.lang.EnumStrings;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandMain implements CommandExecutor {

    private ChatFilter chatFilter;
    private ClearChatCommand clearChatCommand;
    private ReloadCommand reloadCommand;
    private HelpCommand helpCommand;
    private PauseCommand pauseCommand;
    private BlacklistCommand blacklistCommand;
    private WhitelistCommand whitelistCommand;
    private ImportCommand importCommand;


    public CommandMain(ChatFilter chatFilter) {
        this.chatFilter = chatFilter;
        this.clearChatCommand = new ClearChatCommand(this.chatFilter);
        this.reloadCommand = new ReloadCommand(this.chatFilter);
        this.helpCommand = new HelpCommand(this.chatFilter);
        this.pauseCommand = new PauseCommand(this.chatFilter);
        this.blacklistCommand = new BlacklistCommand(this.chatFilter);
        this.whitelistCommand = new WhitelistCommand(this.chatFilter);
        this.importCommand = new ImportCommand(this.chatFilter);

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "clear":
                    this.clearChatCommand.onCommand(sender, cmd, label, args);
                    break;
                case "reload":
                    this.reloadCommand.onCommand(sender, cmd, label, args);
                    break;
                case "pause":
                    this.pauseCommand.onCommand(sender, cmd, label, args);
                    break;
                case "help":
                    this.helpCommand.onCommand(sender, cmd, label, args);
                    break;
                case "blacklist":
                    this.blacklistCommand.onCommand(sender, cmd, label, args);
                    break;
                case "whitelist":
                    this.whitelistCommand.onCommand(sender, cmd, label, args);
                    break;
                case "import":
                    this.importCommand.onCommand(sender, cmd, label, args);
                    break;
                default:
                    sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_ARGS.s)));
            }
        } else {
            sender.sendMessage(chatFilter.colour(chatFilter.getLang().mapToString(EnumStrings.NO_ARGS.s)));
        }
        return true;
    }
}
