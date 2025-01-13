package zxc.mrdrag0nxyt.nightChatRestricter

import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import zxc.mrdrag0nxyt.nightChatRestricter.command.ReloadCommand
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.handler.EventHandler
import zxc.mrdrag0nxyt.nightChatRestricter.util.CanChatPlayers
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessage

class NightChatRestricter : JavaPlugin() {

    private val config = Config(this)
    private val canChatPlayers = CanChatPlayers()

    override fun onEnable() {
        getCommand("reloadnightchatrestricter")?.setExecutor(ReloadCommand(this, config))
        server.pluginManager.registerEvents(EventHandler(canChatPlayers, config), this)

        if (config.enableMetrics) Metrics(this, 23925)

        showEnableTitle(true)
    }

    override fun onDisable() {
        showEnableTitle(false)
    }

    fun reload() {
        config.reload()
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
