package zxc.mrdrag0nxyt.nightChatRestricter.handler

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.util.CanChatPlayers
import zxc.mrdrag0nxyt.nightChatRestricter.util.formatPlayedAndTotalTime
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessageWithPlaceholders

class EventHandler(
    private val canChatPlayers: CanChatPlayers,
    private val config: Config
) : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val player = event.player
        val playerUUID = player.uniqueId
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = config.needTime

        if (playedTime >= needTime) {
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

        if (canChatPlayers.isCanChat(playerUUID)) {
            return
        }

        if (player.hasPermission("ncr.bypass") || player.hasPermission("ncr.bypass.${eventType.eventName}")) {
            return
        }

        if (playedTime >= needTime) {
            canChatPlayers.add(playerUUID)
            return
        }

        val needTimeMap = formatPlayedAndTotalTime(needTime, playedTime)
        val strings = if (eventType == EventType.CHAT) config.reducedChatMessage else config.reducedCommandMessage
        strings.forEach { player.sendColoredMessageWithPlaceholders(it, needTimeMap) }

        event.isCancelled = true
    }

    private enum class EventType(val eventName: String) {
        CHAT("chat"),
        COMMAND("command")
    }

}