package cf.lucasmellof.senior.core.manager.config.instances

import cf.lucasmellof.senior.core.manager.config.Configurable

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 07/08/2020
 */
@Configurable("core-config")
class CoreConfig {
    val token = "<change>"
    val ownerIDs = arrayOf<String>()
    val defaultPrefix = "!"

    val mongoURL = ""
    val mongodbName = ""
}