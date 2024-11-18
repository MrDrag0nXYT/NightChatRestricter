package zxc.mrdrag0nxyt.nightChatRestricter.handler

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Statistic
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import zxc.mrdrag0nxyt.nightChatRestricter.config.Config
import zxc.mrdrag0nxyt.nightChatRestricter.util.formatPlayedAndTotalTime
import zxc.mrdrag0nxyt.nightChatRestricter.util.sendColoredMessageWithPlaceholders

class EventHandler(
    private val plugin: NightChatRestricter,
    private val config: Config
) : Listener {

    @EventHandler
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val yamlConfiguration = config.yamlConfiguration

        val player = event.player
        val playerName = player.name
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = yamlConfiguration.getInt("need-time", 600)

        NightChatRestricter.needPlayedTime[playerName] = needTime

        if (playedTime < needTime) {
            NightChatRestricter.needPlayedTime[playerName] = needTime

        } else if (playedTime >= needTime) {
            NightChatRestricter.canChatPlayers.add(playerName)
        }

    }

    @EventHandler
    fun onChat(event: AsyncChatEvent) {
        val player = event.player
        val playerName = event.player.name
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val yamlConfiguration = config.yamlConfiguration

        if (player.hasPermission("ncr.bypass")) {
            return
        }

        if (NightChatRestricter.canChatPlayers.contains(playerName)) {
            return
        }

        if (NightChatRestricter.needPlayedTime.contains(playerName)) {
            val needTime = NightChatRestricter.needPlayedTime[playerName]!!

            if (playedTime >= needTime) {
                return
            }

            val needTimeMap =
                NightChatRestricter.needPlayedTime[playerName]?.let { formatPlayedAndTotalTime(it, playedTime) }
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