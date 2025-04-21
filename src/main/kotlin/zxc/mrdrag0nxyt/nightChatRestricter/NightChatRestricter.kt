package zxc.mrdrag0nxyt.nightChatRestricter

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import zxc.mrdrag0nxyt.nightChatRestricter.command.ReloadCommand
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.handler.EventHandler
import zxc.mrdrag0nxyt.nightChatRestricter.util.CanChatPlayers

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
        val isEnableMessage = if (isEnabling) "<#ace1af>successfully loaded!" else "<#d45079>successfully unloaded!"

        val consoleCommandSender = Bukkit.getConsoleSender()
        val miniMessage = MiniMessage.miniMessage()

        consoleCommandSender.sendMessage(
            miniMessage.deserialize("<#a880ff>NightChatRestricter ${description.version} <#fcfcfc>by</#fcfcfc> MrDrag0nXYT</#a880ff> $isEnableMessage")
        )
    }
}
