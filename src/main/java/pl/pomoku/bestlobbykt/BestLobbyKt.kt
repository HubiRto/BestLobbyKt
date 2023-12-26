package pl.pomoku.bestlobbykt

import lombok.Getter
import lombok.SneakyThrows
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import java.io.File

@Getter
class BestLobbyKt : JavaPlugin() {
    private lateinit var applicationContext: AnnotationConfigApplicationContext;

    override fun onEnable() {
        databaseConfig = loadCustomConfig("database.yml");
        loadSpringBoot();
    }

    override fun onDisable() {
        applicationContext.close();
    }

    private fun loadSpringBoot() {
        Thread.currentThread().setContextClassLoader(javaClass.getClassLoader())
        applicationContext = AnnotationConfigApplicationContext()
        applicationContext.scan("pl.pomoku.bestlobbykt")
        applicationContext.refresh()
    }

    @SneakyThrows
    private fun loadCustomConfig(fileName: String): FileConfiguration {
        val file = File(dataFolder, fileName)
        if (!file.exists()) {
            file.getParentFile().mkdirs()
            saveResource(fileName, false)
        }
        val config: FileConfiguration = YamlConfiguration()
        config.load(file)
        return config
    }

    companion object {
        lateinit var databaseConfig: FileConfiguration;
    }
}
