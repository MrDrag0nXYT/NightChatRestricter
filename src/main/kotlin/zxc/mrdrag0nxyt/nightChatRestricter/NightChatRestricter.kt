package zxc.mrdrag0nxyt.nightChatRestricter

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import zxc.mrdrag0nxyt.nightChatRestricter.command.NightChatRestricterCommand
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.handler.EventHandler
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessage

class NightChatRestricter : JavaPlugin() {

    companion object {
        var needPlayedTime = HashMap<String, Int>()
        var canChatPlayers = ArrayList<String>()
    }

    private val config = Config(this)

    override fun onEnable() {
        getCommand("nightchatrestricter")?.setExecutor(NightChatRestricterCommand(this, config))
        server.pluginManager.registerEvents(EventHandler(this, config), this)

        showEnableTitle(true)
    }

    override fun onDisable() {
        showEnableTitle(false)
    }

    fun reload() {
        config.reload()
    }


    private fun showEnableTitle(isEnabling: Boolean) {
        val message = if (isEnabling) "<#00ff7f>NCR successfully loaded!" else "<#dc143c>NCR successfully disabled!"

        val consoleCommandSender = Bukkit.getConsoleSender()
        consoleCommandSender.sendColoredMessage(" ");
        consoleCommandSender.sendColoredMessage("<#a880ff>█▄░█ █ █▀▀ █░█ ▀█▀ █▀▀ █░█ ▄▀█ ▀█▀ █▀█ █▀▀ █▀ ▀█▀ █▀█ █ █▀▀ ▀█▀ █▀▀ █▀█</#a880ff>    <#696969>|</#696969>    <#fcfcfc>Version: <#a880ff>${description.version}</#a880ff>");
        consoleCommandSender.sendColoredMessage("<#a880ff>█░▀█ █ █▄█ █▀█ ░█░ █▄▄ █▀█ █▀█ ░█░ █▀▄ ██▄ ▄█ ░█░ █▀▄ █ █▄▄ ░█░ ██▄ █▀▄</#a880ff>    <#696969>|</#696969>    <#fcfcfc>Author: <#a880ff>MrDrag0nXYT (https://drakoshaslv.ru)</#a880ff>");
        consoleCommandSender.sendColoredMessage(" ");
        consoleCommandSender.sendColoredMessage(message);
        consoleCommandSender.sendColoredMessage(" ");
    }
}
