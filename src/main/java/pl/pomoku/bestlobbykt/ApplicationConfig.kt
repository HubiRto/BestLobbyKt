package pl.pomoku.bestlobbykt

import jakarta.persistence.EntityManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import pl.pomoku.bestlobbykt.BestLobbyKt.Companion.databaseConfig
import java.util.*
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(basePackages = ["pl.pomoku.bestlobbykt.repository"])
@EnableTransactionManagement
open class ApplicationConfig {
    @Bean
    open fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val factoryBean = LocalContainerEntityManagerFactoryBean()
        factoryBean.dataSource = getMySQLDataSource()
        factoryBean.setPackagesToScan("pl.pomoku.bestlobbykt.*")
        factoryBean.setPersistenceUnitName("JpaDB")
        val vendorAdapter = HibernateJpaVendorAdapter()
        factoryBean.jpaVendorAdapter = vendorAdapter

        val properties = Properties()
        properties["hibernate.show_sql"] = "false"
        properties["hibernate.format_sql"] = "true"
        properties["hibernate.dialect"] = "org.hibernate.dialect.MariaDBDialect"
        properties["hibernate.hbm2ddl.auto"] = "update"
        factoryBean.setJpaProperties(properties)

        return factoryBean
    }

    @Bean
    open fun getMySQLDataSource(): DataSource {
        val url = createJDBCUrl()
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName("com.mysql.jdbc.Driver")
        dataSource.url = url
        dataSource.username = prop("username")
        dataSource.password = prop("password")
        return dataSource
    }

    private fun createJDBCUrl(): String {
        return "jdbc:mysql://${prop("host")}:${prop("port")}/${prop("database")}"
    }

    private fun prop(path: String): String? {
        return databaseConfig?.getString("database.$path")
    }

    @Bean
    open fun transactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        return transactionManager
    }
}
