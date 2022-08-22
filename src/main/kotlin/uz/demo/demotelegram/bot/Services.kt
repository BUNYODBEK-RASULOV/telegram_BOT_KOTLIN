package uz.demo.demotelegram.bot

import org.springframework.context.annotation.Lazy
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender
import java.util.*

@Service
class UpdateHandlerAdapter(
    private val messageHandler: MessageHandler,
    private val callbackHandler: CallbackHandler,
    private val chatUserRepository: ChatUserRepository
) {
    fun handle(absSender: AbsSender, update: Update) {
        loadLang(update)
        when {
            update.hasMessage() -> {
                messageHandler.handle(absSender, update.message)
            }

            update.hasCallbackQuery() -> {
                callbackHandler.handle(absSender, update.callbackQuery)
            }

            else -> {
                println(update.toString())
            }
        }
    }

    private fun loadLang(update: Update) {
        val chatId = update.getTelegramId()
        val chatUser = chatUserRepository.findByChatIdAndDeletedFalse(chatId)
            ?: chatUserRepository.save(ChatUser(chatId, update.getTelegramUsername()))
        LocaleContextHolder.setLocale(Locale(chatUser.lang.code))
    }
}

interface MessageHandler {
    fun handle(absSender: AbsSender, message: Message)
}

interface CallbackHandler {
    fun handle(absSender: AbsSender, callbackQuery: CallbackQuery)
}

@Service
class MessageSourceService(@Lazy val messageResourceBundleMessageSource: ResourceBundleMessageSource) {

    fun getMessage(sourceKey: LocalizationTextKey): String {
        return messageResourceBundleMessageSource.getMessage(
            sourceKey.name,
            null,
            LocaleContextHolder.getLocale()
        )
    }

    fun getMessage(sourceKey: LocalizationTextKey, locale: Locale): String {
        return messageResourceBundleMessageSource.getMessage(sourceKey.name, null, locale)
    }

    fun getMessage(sourceKey: LocalizationTextKey, any: Array<String>): String {
        return messageResourceBundleMessageSource.getMessage(
            sourceKey.name,
            any,
            LocaleContextHolder.getLocale()
        )
    }
}