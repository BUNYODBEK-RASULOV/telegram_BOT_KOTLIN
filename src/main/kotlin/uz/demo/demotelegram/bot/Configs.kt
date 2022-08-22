package uz.demo.demotelegram.bot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.telegram.telegrambots.meta.generics.LongPollingBot
import java.util.*

@Configuration
@ConfigurationProperties(prefix = "telegram")
class BotProperties {
    lateinit var bots: List<Bot>

    class Bot {
        lateinit var username: String
        lateinit var token: String
    }
}

@Configuration
class BotConfig(
    private val botProperties: BotProperties,
    private val updateHandlerAdapter: UpdateHandlerAdapter
) {

    @Bean
    fun botList(): List<LongPollingBot> {
        return botProperties.bots.map { bot ->
            MainBots(bot.username, bot.token, updateHandlerAdapter)
        }
    }

    @Bean
    fun messageResourceBundleMessageSource(): ResourceBundleMessageSource? {
        val messageSource = ResourceBundleMessageSource()
        messageSource.setBasename("messages")
        messageSource.setCacheSeconds(3600)
        messageSource.setDefaultLocale(Locale.US)
        messageSource.setDefaultEncoding("UTF-8")
        return messageSource
    }
}