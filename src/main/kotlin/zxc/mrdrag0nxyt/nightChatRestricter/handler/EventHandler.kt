package zxc.mrdrag0nxyt.nightChatRestricter.handler

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.title.Title
import net.kyori.adventure.title.TitlePart
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.node.types.PermissionNode
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

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoinEvent(event: PlayerJoinEvent) {
        val player = event.player
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = config.needTime

        if (player.hasPermission("ncr.bypass")) {
            canChatPlayers.add(player.uniqueId)
            return
        }

        if (playedTime >= needTime) {
            allowChatForPlayer(player)
        }
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        val playerUUID = event.player.uniqueId
        canChatPlayers.remove(playerUUID)
    }

    @EventHandler(ignoreCancelled = true)
    fun onChat(event: AsyncChatEvent) {
        reduce(event.player, EventType.CHAT, event)
    }

    @EventHandler(ignoreCancelled = true)
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val commandName = event.message
            .split(" ")[0].substring(1)

        if (config.blockedCommands.contains(commandName)) {
            reduce(event.player, EventType.COMMAND, event)
        }
    }


    private fun reduce(player: Player, eventType: EventType, event: Cancellable) {
        val playerUUID = player.uniqueId
        val playedTime = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val needTime = config.needTime

        if (player.hasPermission("ncr.bypass")) {
            canChatPlayers.add(playerUUID)
            return
        }

        if (playedTime >= needTime) {
            allowChatForPlayer(player)
            return
        }

        if (canChatPlayers.isCanChat(playerUUID) || player.hasPermission("ncr.bypass.${eventType.eventName}")) {
            return
        }

        val needTimeMap = formatPlayedAndTotalTime(needTime, playedTime)
        val strings = if (eventType == EventType.CHAT) config.reducedChatMessage else config.reducedCommandMessage

        strings.forEach { player.sendColoredMessageWithPlaceholders(it, needTimeMap) }
        sendRestrictionTitle(player)

        event.isCancelled = true
    }

    private fun allowChatForPlayer(player: Player) {
        val luckPerms = LuckPermsProvider.get()

        luckPerms.userManager.loadUser(player.uniqueId).thenAccept { user ->
            if (user == null) return@thenAccept

            val permissionNode = PermissionNode.builder("ncr.bypass")
                .value(true)
                .build()

            user.data().add(permissionNode)
            luckPerms.userManager.saveUser(user)
        }

        canChatPlayers.add(player.uniqueId)
    }


    private enum class EventType(val eventName: String) {
        CHAT("chat"),
        COMMAND("command")
    }

    private fun sendRestrictionTitle(player: Player) {
        if (!config.isTitleEnabled) return

        val title = config.title
        val subtitle = config.subtitle
        val actionbar = config.actionbar

        player.sendTitlePart(TitlePart.TITLE, title)
        player.sendTitlePart(TitlePart.SUBTITLE, subtitle)
        player.sendTitlePart(
            TitlePart.TIMES, Title.Times.times(
                config.titleFadeIn,
                config.titleStay,
                config.titleFadeOut,
            )
        )
        player.sendActionBar(actionbar)
    }

}