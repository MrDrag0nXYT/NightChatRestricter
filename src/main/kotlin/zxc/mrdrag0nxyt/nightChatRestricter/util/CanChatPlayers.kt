package zxc.mrdrag0nxyt.nightChatRestricter.util

import java.util.*

class CanChatPlayers {
    private val canChatPlayers = mutableSetOf<UUID>()

    fun add(uuid: UUID) {
        canChatPlayers.add(uuid)
    }

    fun remove(uuid: UUID) {
        canChatPlayers.remove(uuid)
    }

    fun isCanChat(uuid: UUID): Boolean {
        return uuid in canChatPlayers
    }
}