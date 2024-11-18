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
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.util.formatPlayedAndTotalTime
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessageWithPlaceholders

class EventHandler(
    private val config: Config
) : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val yamlConfiguration = config.yamlConfiguration

        val player = event.player
        val playerName = player.name
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = yamlConfiguration.getInt("need-time", 600)

        if (playedTime >= needTime) {
            NightChatRestricter.canChatPlayers.add(playerName)
        }
    }

    @EventHandler
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val playerName = event.player.name
        NightChatRestricter.canChatPlayers.remove(playerName)
    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        reduce(event.player, "chat", event)
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (NightChatRestricter.blockedCommands.contains(event.message.substring(1))) {
            reduce(event.player, "command", event)
        }
    }


    private fun reduce(player: Player, messageType: String, event: Cancellable) {
        val yamlConfiguration = config.yamlConfiguration

        val playerName = player.name
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = yamlConfiguration.getInt("need-time", 600)

        if (NightChatRestricter.canChatPlayers.contains(playerName)) {
            return
        }

        if (player.hasPermission("ncr.bypass") || player.hasPermission("ncr.bypass.$messageType")) {
            return
        }

        if (playedTime >= needTime) {
            NightChatRestricter.canChatPlayers.add(playerName)
            return
        }

        val needTimeMap = formatPlayedAndTotalTime(needTime, playedTime)
        for (string in yamlConfiguration.getStringList("messages.reduced-${messageType}")) {
            player.sendColoredMessageWithPlaceholders(string, needTimeMap)
        }
        event.isCancelled = true
    }

}