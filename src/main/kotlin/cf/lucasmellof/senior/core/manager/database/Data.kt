package cf.lucasmellof.senior.core.manager.database

/* 
 * @author Lucasmellof, Lucas de Mello Freitas created on 07/08/2020
 */
data class Guild(
    val id: String,
    val prefix: String = "!",
    val volume: Int = 10,
    val djRoleID: Long = 0L
)