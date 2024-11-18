package zxc.mrdrag0nxyt.nightChatRestricter

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import zxc.mrdrag0nxyt.nightChatRestricter.command.NightChatRestricterCommand
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.handler.EventHandler
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessage

class NightChatRestricter : JavaPlugin() {

    companion object {
        var blockedCommands = ArrayList<String>()
        var canChatPlayers = ArrayList<String>()
    }

    private val config = Config(this)

    override fun onEnable() {
        getCommand("nightchatrestricter")?.setExecutor(NightChatRestricterCommand(this, config))
        server.pluginManager.registerEvents(EventHandler(config), this)

        if (config.yamlConfiguration.getBoolean("enable-metrics", true)) {
            val metrics: Metrics = Metrics(this, 23925)
        }

        blockedCommands = config.yamlConfiguration.getStringList("blocked-commands") as ArrayList<String>

        showEnableTitle(true)
    }

    override fun onDisable() {
        showEnableTitle(false)
    }

    fun reload() {
        config.reload()
        blockedCommands = config.yamlConfiguration.getStringList("blocked-commands") as ArrayList<String>
    }


    private fun showEnableTitle(isEnabling: Boolean) {
        val message = if (isEnabling) "<#00ff7f>loaded</#00ff7f>" else "<#dc143c>disabled</#dc143c>"

        val consoleCommandSender = Bukkit.getConsoleSender()
        consoleCommandSender.sendColoredMessage(" ");
        consoleCommandSender.sendColoredMessage("<#a880ff>███╗░░██╗░█████╗░██████╗░</#a880ff>");
        consoleCommandSender.sendColoredMessage("<#a880ff>████╗░██║██╔══██╗██╔══██╗</#a880ff>    <#696969>╔</#696969> <#fcfcfc>Version: <#a880ff>${description.version}</#a880ff>")
        consoleCommandSender.sendColoredMessage("<#a880ff>██╔██╗██║██║░░╚═╝██████╔╝</#a880ff>    <#696969>║</#696969> <#fcfcfc>Author: <#a880ff>MrDrag0nXYT (https://drakoshaslv.ru)</#a880ff>")
        consoleCommandSender.sendColoredMessage("<#a880ff>██║╚████║██║░░██╗██╔══██╗</#a880ff>    <#696969>║</#696969>   <#696969>-</#696969> <#fcfcfc>For <#a880ff>NightShard (https://nshard.ru)</#a880ff>")
        consoleCommandSender.sendColoredMessage("<#a880ff>██║░╚███║╚█████╔╝██║░░██║</#a880ff>    <#696969>╚</#696969> <#fcfcfc>NightChatRestricter successfully ${message}!")
        consoleCommandSender.sendColoredMessage("<#a880ff>╚═╝░░╚══╝░╚════╝░╚═╝░░╚═╝</#a880ff>");
        consoleCommandSender.sendColoredMessage(" ");
    }
}
