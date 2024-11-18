package zxc.mrdrag0nxyt.nightChatRestricter

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import zxc.mrdrag0nxyt.nightChatRestricter.command.NightChatRestricterCommand
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.database.DatabaseManager
import zxc.mrdrag0nxyt.nightChatRestricter.handler.EventHandler
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessage
import java.util.ArrayList

class NightChatRestricter : JavaPlugin() {

    companion object {
        var needPlayedTime = HashMap<String, Long>()
        var canChatPlayers = ArrayList<String>()
    }

    private val config = Config(this)
    private val databaseManager: DatabaseManager = DatabaseManager(this, config)

    override fun onEnable() {
        getCommand("nightchatrestricter")?.setExecutor(NightChatRestricterCommand(this, config))
        server.pluginManager.registerEvents(EventHandler(this, config, databaseManager), this)

        val databaseWorker = databaseManager.databaseWorker
        try {
            databaseManager.getConnection().use { connection ->
                databaseWorker!!.initTable(connection!!)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        initMessage()
    }

    override fun onDisable() {
        databaseManager.closeConnection()
    }

    fun reload() {
        config.reload()
//        databaseManager.updateConnection(config.yamlConfiguration)
    }


    private fun initMessage() {
        val consoleCommandSender = Bukkit.getConsoleSender()
        consoleCommandSender.sendColoredMessage(" ")
        consoleCommandSender.sendColoredMessage("<#a880ff>NightChatRestricter</#a880ff>   <#c0c0c0>|</#c0c0c0>   <#fffafa>v. ${description.version}</#fffafa>")
        consoleCommandSender.sendColoredMessage(" ")
    }
}
