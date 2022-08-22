package uz.demo.demotelegram.bot.handlers

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender
import uz.demo.demotelegram.bot.*
import uz.demo.demotelegram.bot.CallbackType.CHOOSE_LANGUAGE
import uz.demo.demotelegram.bot.CallbackType.CHOOSE_SHARE_TEXT
import uz.demo.demotelegram.bot.LocalizationTextKey.SEND_CONTACT_MESSAGE
import uz.demo.demotelegram.bot.LocalizationTextKey.SEND_TEXT_MESSAGE
import uz.demo.demotelegram.bot.Steps.ENTER_TEXT
import uz.demo.demotelegram.bot.Steps.SHARE_CONTACT
import java.util.*


@Service
class CallbackHandlerImpl(
    private val chatUserRepository: ChatUserRepository,
    private val messageRepository: ChatMessageRepository,
    private val messageSourceService: MessageSourceService
) : CallbackHandler {
    override fun handle(sender: AbsSender, callbackQuery: CallbackQuery) {
        val chatId = callbackQuery.from.id.toString()
        val chatUser = chatUserRepository.findByChatIdAndDeletedFalse(callbackQuery.from.id)!!
        val step = chatUser.step
        val message = callbackQuery.message
        val messageId = message.messageId

        val answerCallbackQuery = AnswerCallbackQuery()
        answerCallbackQuery.callbackQueryId = callbackQuery.id

        val editMessage = EditMessageText()
        editMessage.enableHtml(true)
        editMessage.chatId = chatId
        editMessage.messageId = messageId

        val sendMessage = SendMessage()
        sendMessage.enableHtml(true)
        sendMessage.chatId = chatId

//        println(callbackQuery.data)

        val split = callbackQuery.data.split("#")

        when (CallbackType.valueOf(split[0])) {
            CHOOSE_LANGUAGE -> {
                val language = Language.valueOf(split[1])
                chatUser.lang = language
                LocaleContextHolder.setLocale(Locale(language.code))
                sender.deleteMessages(chatId, messageId)

                sendMessage.text = messageSourceService.getMessage(SEND_CONTACT_MESSAGE)
                sendMessage.replyMarkup = shareContactMessage(messageSourceService)
                chatUser.lastMessageId = sender.sendMessage(sendMessage)
                chatUser.step = SHARE_CONTACT
                chatUserRepository.save(chatUser)
            }

            CHOOSE_SHARE_TEXT -> {
                editMessage.text = messageSourceService.getMessage(SEND_TEXT_MESSAGE)
                sender.editMessage(editMessage)
                chatUser.step = ENTER_TEXT
                chatUserRepository.save(chatUser)
            }

        }

        sender.answerCallBackQuery(answerCallbackQuery)
    }

}