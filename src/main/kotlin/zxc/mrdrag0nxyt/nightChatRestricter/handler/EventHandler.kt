package zxc.mrdrag0nxyt.nightChatRestricter.handler

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.database.DatabaseManager
import zxc.mrdrag0nxyt.nightChatRestricter.util.CanChatPlayers
import zxc.mrdrag0nxyt.nightChatRestricter.util.formatPlayedAndTotalTime
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessageWithPlaceholders
import java.util.*

class EventHandler(
    private val plugin: NightChatRestricter,
    private val canChatPlayers: CanChatPlayers,
    private val config: Config,
    private val databaseManager: DatabaseManager
) : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val player = event.player
        val playerUUID = player.uniqueId
        val playerName = player.name
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = config.needTime

        isPlayerExistInDatabase(playerUUID, playerName)

        if (playedTime >= needTime || player.hasPermission("ncr.bypass")) {
            addPlayerToDatabase(player)
            canChatPlayers.add(playerUUID)
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val playerUUID = event.player.uniqueId
        canChatPlayers.remove(playerUUID)
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        reduce(event.player, EventType.CHAT, event)
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (config.blockedCommands.contains(event.message.substring(1))) {
            reduce(event.player, EventType.COMMAND, event)
        }
    }


    private fun reduce(player: Player, eventType: EventType, event: Cancellable) {
        val playerUUID = player.uniqueId
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = config.needTime

        if (playedTime >= needTime || player.hasPermission("ncr.bypass")) {
            canChatPlayers.add(playerUUID)
            addPlayerToDatabase(player)
            return
        }

        if (canChatPlayers.isCanChat(playerUUID) || player.hasPermission("ncr.bypass.${eventType.eventName}")) {
            return
        }

        val needTimeMap = formatPlayedAndTotalTime(needTime, playedTime)
        val strings = if (eventType == EventType.CHAT) config.reducedChatMessage else config.reducedCommandMessage
        strings.forEach { player.sendColoredMessageWithPlaceholders(it, needTimeMap) }

        event.isCancelled = true
    }

    private fun isPlayerExistInDatabase(playerUUID: UUID, playerName: String) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val connection = databaseManager.getConnection()
                val databaseWorker = databaseManager.databaseWorker

                val isPlayerFound = if (config.useUUID) {
                    databaseWorker?.isPlayerExistByUUID(connection!!, playerUUID.toString())
                } else {
                    databaseWorker?.isPlayerExistByName(connection!!, playerName)
                }

                if (isPlayerFound == true) {
                    canChatPlayers.add(playerUUID)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun addPlayerToDatabase(player: Player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            try {
                val connection = databaseManager.getConnection()
                val databaseWorker = databaseManager.databaseWorker
                databaseWorker?.addPlayer(connection!!, player)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }


    private enum class EventType(val eventName: String) {
        CHAT("chat"),
        COMMAND("command")
    }

}