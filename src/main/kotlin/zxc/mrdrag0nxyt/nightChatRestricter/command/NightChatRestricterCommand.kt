package zxc.mrdrag0nxyt.nightChatRestricter.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessage

class NightChatRestricterCommand(
    private val plugin: NightChatRestricter,
    private val config: Config
) : CommandExecutor, TabCompleter {

    override fun onCommand(
        commandSender: CommandSender,
        command: Command,
        str: String,
        strings: Array<String>?
    ): Boolean {

        val yamlConfiguration = config.yamlConfiguration

        if (!commandSender.hasPermission("ncr.command")) {
            commandSender.sendColoredMessage(
                yamlConfiguration.getString(
                    "messages.no-permission",
                    "<#dc143c>У вас недостаточно прав для выполнения этого действия!"
                )!!
            )
            return false
        }

        when (strings?.get(0)?.lowercase()) {
            "reload" -> {
                plugin.reload()
                commandSender.sendColoredMessage(
                    yamlConfiguration.getString(
                        "messages.command.reloaded",
                        "<#00ff7f>NightChatRestricter успешно перезагружен!"
                    )!!
                )
            }

            else -> {
                commandSender.sendColoredMessage(
                    yamlConfiguration.getString(
                        "messages.command.usage",
                        "<#fffafa><#a880ff>NightChatRestricter</#a880ff> - Использование: <#a880ff><click:suggest_command:'/nightchatrestricter reload'>/nightchatrestricter reload</click></#a880ff>"
                    )!!
                )
            }
        }

        return true
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command,
        str: String,
        strings: Array<String>?
    ): List<String>? {

        if (strings?.size == 1) {
            return listOf("reload")
        }

        return null
    }

}