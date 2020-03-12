package kr.jadekim.db.exposed

import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.concurrent.Executors
import javax.sql.DataSource
import kotlin.coroutines.coroutineContext

open class ReadDB(
    dataSource: DataSource,
    threadCount: Int = 4
) {

    protected val readDB = Database.connect(dataSource)
    protected val dispatcher = Executors.newFixedThreadPool(threadCount).asCoroutineDispatcher()

    open suspend fun <T> read(statement: suspend Transaction.() -> T): T {
        return withContext(coroutineContext + dispatcher) {
            newSuspendedTransaction(db = readDB, statement = statement)
        }
    }
}

open class CrudDB(
    dataSource: DataSource,
    readOnlyDataSource: DataSource = dataSource,
    threadCount: Int = 4
) : ReadDB(readOnlyDataSource, threadCount) {

    protected val crudDB = Database.connect(dataSource)

    suspend fun <T> execute(statement: suspend Transaction.() -> T): T {
        return withContext(coroutineContext + dispatcher) {
            newSuspendedTransaction(db = crudDB, statement = statement)
        }
    }
}

typealias DB = CrudDB