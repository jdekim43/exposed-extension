# exposed-extension
Exposed 를 사용할 때 자주 사용하는 함수 라이브러리
* Database class
* DB functions (Exposed expression)
    * Concat(Expression<*>, String)
* Extension functions

## Install
### Gradle Project
1. Add dependency
    ```
    build.gradle.kts
   
    implementation("kr.jadekim:exposed-extension:1.0.0")
    ```

## How to use
### Database class
```
// Only read
val dataSource = ...
val threadCount = 4 //Coroutine thread pool count
val db = ReadDB(dataSource, threadCount)

// CRUD
val dataSource = ...
val readDataSource = dataSource
val threadCount = 4 //Coroutine thread pool count
val db = CrudDB(dataSource, readDataSource, threadCount)

or

val db = DB(dataSource, readDataSource, threadCount)
```
### Extension functions
#### upsert
```
Table.upsert(
    where = { Table.id eq 1 },
    onInsert = {
        ...
    },
    onUpdate = {
        ...
    }
)
```