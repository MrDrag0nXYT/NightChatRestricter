package zxc.mrdrag0nxyt.nightChatRestricter.handler

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Statistic
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.database.DatabaseManager
import zxc.mrdrag0nxyt.nightChatRestricter.util.formatTime
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessageWithPlaceholders

class EventHandler(
    private val plugin: NightChatRestricter,
    private val config: Config,
    private val databaseManager: DatabaseManager
) : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val yamlConfiguration = config.yamlConfiguration

        val player = event.player
        val playerName = player.name
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE)
        var needTime: Long? = null

        val databaseWorker = databaseManager.databaseWorker
        try {
            needTime = databaseManager.getConnection()?.use { connection ->
                databaseWorker?.getNeedPlayedTime(connection, playerName)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (needTime == null) {
            needTime = yamlConfiguration.getLong("need-time", 600)
            NightChatRestricter.needPlayedTime[playerName] = needTime

            try {
                databaseManager.getConnection()?.use { connection ->
                    databaseWorker?.addPlayer(connection, playerName, needTime)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (playedTime < needTime) {
            NightChatRestricter.needPlayedTime[playerName] = needTime

        } else if (playedTime >= needTime) {
            NightChatRestricter.canChatPlayers.add(playerName)
        }

    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        val playerName = event.player.name
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE)
        val yamlConfiguration = config.yamlConfiguration

        if (NightChatRestricter.canChatPlayers.contains(playerName)) {
            return
        }

        if (NightChatRestricter.needPlayedTime.contains(playerName)) {
            val needTime = NightChatRestricter.needPlayedTime[playerName]!!

            if (playedTime >= needTime) {
                return
            }

            val needTimeMap = NightChatRestricter.needPlayedTime[playerName]?.let { formatTime(it) }
            if (needTimeMap != null) {
                for (string in yamlConfiguration.getStringList("messages.reduced")) {
                    player.sendColoredMessageWithPlaceholders(string, needTimeMap)
                }
            }
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val playerName = event.player.name

        NightChatRestricter.needPlayedTime.remove(playerName)
        NightChatRestricter.canChatPlayers.remove(playerName)
    }

}