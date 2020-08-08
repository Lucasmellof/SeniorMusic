package cf.lucasmellof.senior.core.manager.config

import cf.lucasmellof.senior.core.manager.config.instances.CoreConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.findAnnotation

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 07/08/2020
 */
val dataFolder = File(System.getProperty("user.dir"), "data")
val gson: Gson = GsonBuilder().setPrettyPrinting().create()

val coreConfig = loadConfig<CoreConfig>(CoreConfig::class)!!

inline fun <reified T : Any> loadConfig(clazz: KClass<*>): T? {
    val ann = clazz.findAnnotation<Configurable>() ?: return null
    dataFolder.mkdirs()
    val configfile = File(dataFolder, ann.name + ".json")
    if (!configfile.exists()) {
        val instance = clazz.createInstance() as T
        val json = gson.toJson(instance)
        configfile.writeText(json, StandardCharsets.UTF_8)
        return instance
    }
    return gson.fromJson(configfile.readText(StandardCharsets.UTF_8), T::class.java) as T
}
