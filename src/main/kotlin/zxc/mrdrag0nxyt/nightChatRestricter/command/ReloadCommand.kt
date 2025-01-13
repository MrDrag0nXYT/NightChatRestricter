package zxc.mrdrag0nxyt.nightChatRestricter.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessage

class ReloadCommand(
    private val plugin: NightChatRestricter,
    private val config: Config
) : CommandExecutor, TabCompleter {

    override fun onCommand(
        commandSender: CommandSender,
        command: Command,
        str: String,
        strings: Array<String>?
    ): Boolean {
        if (!commandSender.hasPermission("ncr.reload")) {
            commandSender.sendColoredMessage(config.noPermissionMessage)
            return false
        }

        plugin.reload()
        commandSender.sendColoredMessage(config.reloadedMessage)

        return true
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command,
        str: String,
        strings: Array<String>?
    ): List<String> {
        return listOf()
    }

}