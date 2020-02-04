package kr.jadekim.db.exposed

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.exceptions.EntityNotFoundException
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import javax.sql.DataSource

open class ReadDB(
    dataSource: DataSource
) {

    private val database = Database.connect(dataSource)

    suspend fun <T> read(statement: suspend Transaction.() -> T): T = try {
        newSuspendedTransaction(Dispatchers.IO, db = database, statement = statement)
    } catch (e: EntityNotFoundException) {
        throw QueryEmptyResultException(e)
    } catch (e: NoSuchElementException) {
        throw QueryEmptyResultException(e)
    }
}

open class CrudDB(
    dataSource: DataSource,
    readOnlyDataSource: DataSource
) : ReadDB(readOnlyDataSource) {

    private val database = Database.connect(dataSource)

    suspend fun <T> execute(statement: suspend Transaction.() -> T): T {
        return newSuspendedTransaction(Dispatchers.IO, db = database, statement = statement)
    }
}

typealias DB = CrudDB

class QueryEmptyResultException(cause: Throwable?) : RuntimeException(cause?.message, cause)