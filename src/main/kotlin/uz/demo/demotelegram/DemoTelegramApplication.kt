package uz.demo.demotelegram

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import uz.demo.demotelegram.bot.BaseRepositoryImpl

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl::class)
class DemoTelegramApplication

fun main(args: Array<String>) {
    runApplication<DemoTelegramApplication>(*args)
}
