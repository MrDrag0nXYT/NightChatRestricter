package zxc.mrdrag0nxyt.nightChatRestricter.config

import org.bukkit.configuration.file.YamlConfiguration
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import java.io.File

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

    var useUUID: Boolean = true
        private set

    var databaseType: DatabaseType = DatabaseType.SQLITE
        private set
    var databaseConfig: DatabaseConfigEntity? = null
        private set

    var reducedChatMessage: List<String> = listOf()
        private set
    var reducedCommandMessage: List<String> = listOf()
        private set
    var noPermissionMessage: String = ""
        private set
    var reloadedMessage: String = ""
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
            checkConfigValue("blocked-commands", HashSet(yamlConfiguration.getStringList("blocked-commands")))
        useUUID = checkConfigValue("use-uuid", yamlConfiguration.getBoolean("use-uuid", true))

        databaseType = DatabaseType.fromStringType(
            checkConfigValue("database.type", yamlConfiguration.getString("database.type", "SQLITE")!!)
        )
        if (databaseType != DatabaseType.SQLITE) {
            databaseConfig = DatabaseConfigEntity(
                host = checkConfigValue(
                    "database.host",
                    yamlConfiguration.getString("database.host", "localhost")!!
                ),
                port = checkConfigValue(
                    "database.port",
                    yamlConfiguration.getInt("database.port", 3306)
                ),
                username = checkConfigValue(
                    "database.username",
                    yamlConfiguration.getString("database.username", "notavailable")!!
                ),
                password = checkConfigValue(
                    "database.password",
                    yamlConfiguration.getString("database.password", "notavailable")!!
                ),
                database = checkConfigValue(
                    "database.name",
                    yamlConfiguration.getString("database.database", "ncr")!!
                ),
            )
        }

        reducedChatMessage = checkConfigValue(
            "messages.reduced-chat",
            yamlConfiguration.getStringList("messages.reduced-chat").ifEmpty {
                listOf("<#fcfcfc>Чтобы писать в чат, необходимо наиграть <#a880ff>%minutes% минут</#a880ff>, вы наиграли <#a880ff>%played_minutes% минут %played_seconds% секунд</#a880ff>")
            }
        )
        reducedCommandMessage = checkConfigValue(
            "messages.reduced-command",
            yamlConfiguration.getStringList("messages.reduced-command").ifEmpty {
                listOf("<#fcfcfc>Чтобы использовать эту команду, необходимо наиграть <#a880ff>%minutes% минут</#a880ff>, вы наиграли <#a880ff>%played_minutes% минут %played_seconds% секунд</#a880ff>")
            }
        )
        noPermissionMessage = checkConfigValue(
            "messages.no-permission",
            yamlConfiguration.getString("messages.no-permission")
                ?: "<#dc143c>У вас недостаточно прав для выполнения этого действия!"
        )
        reloadedMessage = checkConfigValue(
            "messages.command.reloaded",
            yamlConfiguration.getString("messages.command.reloaded")
                ?: "<#00ff7f>NightChatRestricter успешно перезагружен!"
        )

        save()
    }

    private fun <T> checkConfigValue(key: String, value: T): T {
        if (!yamlConfiguration.contains(key)) {
            yamlConfiguration.set(key, value)
        }
        return value
    }

}