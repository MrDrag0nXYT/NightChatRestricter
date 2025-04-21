package zxc.mrdrag0nxyt.nightChatRestricter.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.util.Ticks
import org.bukkit.configuration.file.YamlConfiguration
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import java.io.File
import java.time.Duration

class Config(
    private val plugin: NightChatRestricter
) {

    private val fileName = "config.yml"
    private val file: File = File(plugin.dataFolder, fileName)
    private val yamlConfiguration: YamlConfiguration = loadConfiguration()

    var enableMetrics: Boolean = true
        private set
    var needTime: Int = 600
        private set
    var blockedCommands: HashSet<String> = hashSetOf()
        private set

    var isTitleEnabled = false
        private set
    var title = Component.empty().asComponent()
        private set
    var subtitle = Component.empty().asComponent()
        private set
    var actionbar = Component.empty().asComponent()
        private set
    var titleFadeIn: Duration = Ticks.duration(10)
        private set
    var titleStay: Duration = Ticks.duration(70)
        private set
    var titleFadeOut: Duration = Ticks.duration(20)
        private set

    var reducedChatMessage: List<String> = listOf()
        private set
    var reducedCommandMessage: List<String> = listOf()
        private set
    var noPermissionMessage = Component.empty().asComponent()
        private set
    var reloadedMessage = Component.empty().asComponent()
        private set

    init {
        loadValues()
    }

    private fun loadConfiguration(): YamlConfiguration {
        if (!file.exists()) {
            plugin.saveResource(fileName, false)
        }
        return YamlConfiguration.loadConfiguration(file)
    }

    fun reload() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false)
        }
        try {
            yamlConfiguration.load(file)
            loadValues()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save() {
        try {
            yamlConfiguration.save(file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadValues() {
        enableMetrics = checkConfigValue("enable-metrics", yamlConfiguration.getBoolean("enable-metrics", true))
        needTime = checkConfigValue("need-time", yamlConfiguration.getInt("need-time", 600))
        blockedCommands =
            checkConfigValue("blocked-commands", yamlConfiguration.getStringList("blocked-commands")).toHashSet()

        val miniMessage = MiniMessage.miniMessage()

        isTitleEnabled = checkConfigValue("title.enabled", isTitleEnabled)

        title = checkConfigValue("title.title", "<#a880ff>Не-а!")
            .let { miniMessage.deserialize(it) }
        subtitle = checkConfigValue("title.subtitle", "<#fcfcfc>Ваш чат ещё <#dc143c>заблокирован</#dc143c>")
            .let { miniMessage.deserialize(it) }
        actionbar = checkConfigValue("title.actionbar", "")
            .let { miniMessage.deserialize(it) }

        titleFadeIn = Ticks.duration(checkConfigValue("title.time.fade-in", 10))
        titleFadeIn = Ticks.duration(checkConfigValue("title.time.stay", 70))
        titleFadeOut = Ticks.duration(checkConfigValue("title.time.fade-out", 20))

        val legacyComponentSerializer = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build()

        reducedChatMessage = checkConfigValue(
            "messages.reduced-chat",
            yamlConfiguration.getStringList("messages.reduced-chat").ifEmpty {
                listOf("<#fcfcfc>Чтобы писать в чат, необходимо наиграть <#a880ff>%minutes% минут</#a880ff>, вы наиграли <#a880ff>%played_minutes% минут %played_seconds% секунд</#a880ff>")
            }
        ).map { str ->
            val component = miniMessage.deserialize(str)
            legacyComponentSerializer.serialize(component)
        }

        reducedCommandMessage = checkConfigValue(
            "messages.reduced-command",
            yamlConfiguration.getStringList("messages.reduced-command").ifEmpty {
                listOf("<#fcfcfc>Чтобы использовать эту команду, необходимо наиграть <#a880ff>%minutes% минут</#a880ff>, вы наиграли <#a880ff>%played_minutes% минут %played_seconds% секунд</#a880ff>")
            }
        ).map { str ->
            val component = miniMessage.deserialize(str)
            legacyComponentSerializer.serialize(component)
        }

        noPermissionMessage = checkConfigValue(
            "messages.no-permission",
            "<#dc143c>У вас недостаточно прав для выполнения этого действия!"
        ).let { miniMessage.deserialize(it) }

        reloadedMessage = checkConfigValue(
            "messages.command.reloaded",
            "<#00ff7f>NightChatRestricter успешно перезагружен!"
        ).let { miniMessage.deserialize(it) }

        save()
    }

    private fun <T> checkConfigValue(key: String, value: T): T {
        return if (!yamlConfiguration.contains(key)) {
            yamlConfiguration.set(key, value)
            value
        } else {
            yamlConfiguration.get(key) as T
        }
    }

}