package me.gsqfi.fititle.fititle.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainCmd extends ICmd{
    public MainCmd() {
        super("fititle", null);
        new HelpCmd(this);
        new ReloadCmd(this);
        new ConfirmCmd(this);
        new OpenCmd(this);
        new RemoveCmd(this);
        new SetCmd(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        int length = args.length;
        ICmd subCmd = null;
        if (length > 0){
            String lowerCase = args[0].toLowerCase();
            if (this.getSubCmdNames().contains(lowerCase)) {
                subCmd = this.getSubCmdMap().get(lowerCase);
            }
        }
        if (subCmd == null) subCmd = getHelpCmd(this);
        String permission = "fititle.cmd"+subCmd.getName();
        if (!sender.hasPermission(permission)) {
            sender.sendMessage("§cYou don’t have permission!");
            return false;
        }
        subCmd.onCommand(sender, cmd, label, this.removeOneArg(args));
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        int length = args.length;
        if (length < 1) return this.getSubCmdNames();
        String lowerCase = args[0].toLowerCase();
        if (length == 1) return this.getSubCmdNames().stream().filter(s->s.startsWith(lowerCase)).collect(Collectors.toList());
        if (this.getSubCmdNames().contains(lowerCase)) {
            ICmd subCmd = this.getSubCmdMap().get(lowerCase);
            if (subCmd == null) subCmd = getHelpCmd(this);
            String permission = "fititle.cmd"+subCmd.getName();
            if (!sender.hasPermission(permission)) {
                return null;
            }
            return subCmd.onTabComplete(sender, cmd, label, this.removeOneArg(args));
        }
        return null;
    }

    public static ICmd getHelpCmd(ICmd mainCmd){
        return mainCmd.getSubCmdMap().get("help");
    }
}
