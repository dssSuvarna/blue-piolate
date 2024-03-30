package com.bluepilot.coreservice

import com.bluepilot.configs.RoleMigrationService
import org.flywaydb.core.Flyway
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.sql.Connection
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.util.function.Supplier


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BaseTestConfig {

    @Autowired
    private lateinit var roleMigrationService: RoleMigrationService

    companion object {
        val postgresContainer = PostgreSQLContainer<Nothing>(DockerImageName.parse("postgres:14"))
            .apply {
                withDatabaseName("blue_pilot")
                withUsername("postgres") // Default username for PostgreSQL is 'postgres'
                withPassword("test")
                start()
            }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            registry.add("api-config.openApis",
                Supplier {
                    listOf(
                        "/employee/user-context/validate",
                        "/employee/user-context/update",
                        "/employee/user-context/view",
                        "/admin/user-context/view",
                        "/employee/document/update",
                        "/employee/document/presigned-url"
                    )
                })
        }

        @AfterAll
        fun tearDown() {
            postgresContainer.stop()
        }

        @BeforeAll
        fun startContainer() {
            postgresContainer.start()
        }
    }

    @BeforeEach
    fun flywayMigrate() {
        truncateAllTablesInAllSchemas()
        Flyway.configure()
            .dataSource(postgresContainer.jdbcUrl, postgresContainer.username, postgresContainer.password)
            .load()
            .migrate()
        roleMigrationService.migrateRoles()
    }


    fun truncateAllTablesInAllSchemas() {
        val connection: Connection = DriverManager.getConnection(
            postgresContainer.jdbcUrl,
            postgresContainer.username,
            postgresContainer.password
        )
        val statement = connection.createStatement()
        val schemas = listOf("core_service", "user_service", "auth_service", "notification_service", "salary", "public")
        val metaData: DatabaseMetaData = connection.metaData

        val schemasResultSet = metaData.schemas
        while (schemasResultSet.next()) {
            val schemaName = schemasResultSet.getString("TABLE_SCHEM")
            if (schemas.contains(schemaName)) {
                val tablesResultSet = metaData.getTables(null, schemaName, null, arrayOf("TABLE"))
                while (tablesResultSet.next()) {
                    val tableName = tablesResultSet.getString("TABLE_NAME")
                    // Truncate each table in the schema
                    val truncateQuery = "DROP TABLE \"$schemaName\".\"$tableName\" CASCADE;"
                    statement.execute(truncateQuery)
                }
                tablesResultSet.close()
            }
        }
        schemasResultSet.close()
        connection.close()
    }
}
