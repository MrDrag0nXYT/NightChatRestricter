package zxc.mrdrag0nxyt.nightChatRestricter.util

import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import zxc.mrdrag0nxyt.nightChatRestricter.util.Utilities.miniMessage

object Utilities {
    val miniMessage = MiniMessage.miniMessage()
}

fun CommandSender.sendColoredMessageWithPlaceholders(string: String, values: Map<String, Int>) {
    var result = string

    values.forEach { (key, value) ->
        result = result.replace("%$key%", value.toString())
    }

    this.sendMessage(result)
}

fun formatTime(timeInSeconds: Int): Map<String, Int> {
    val hours = timeInSeconds / 3600
    val minutes = (timeInSeconds % 3600) / 60
    val seconds = timeInSeconds % 60

    return mapOf("hours" to hours, "minutes" to minutes, "seconds" to seconds)
}

fun formatPlayedAndTotalTime(needTimeSeconds: Int, playedTimeSeconds: Int): Map<String, Int> {
    val needTimeMap = formatTime(needTimeSeconds)
    val playedTimeMap = formatTime(playedTimeSeconds).mapKeys { (key, _) -> "played_$key" }

    return needTimeMap + playedTimeMap
}
