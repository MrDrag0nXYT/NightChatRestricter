package zxc.mrdrag0nxyt.nightChatRestricter.config

import org.bukkit.configuration.file.YamlConfiguration
import zxc.mrdrag0nxyt.nightChatRestricter.NightChatRestricter
import java.io.File

class Config(
    private val plugin: NightChatRestricter
) {

    private val fileName = "config.yml"
    private val file: File = File(plugin.dataFolder, fileName)
    val yamlConfiguration: YamlConfiguration = loadConfiguration()

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

}