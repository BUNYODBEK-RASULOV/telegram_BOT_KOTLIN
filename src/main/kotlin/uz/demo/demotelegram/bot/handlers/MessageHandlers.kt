package uz.demo.demotelegram.bot.handlers

import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import uz.demo.demotelegram.bot.*
import uz.demo.demotelegram.bot.LocalizationTextKey.CHOOSE_LANGUAGE_MESSAGE
import uz.demo.demotelegram.bot.LocalizationTextKey.MAIN_MENU_MESSAGE
import uz.demo.demotelegram.bot.Steps.*
import java.io.File
import java.util.*

@Service
class MessageHandlerImpl(
    private val chatUserRepository: ChatUserRepository,
    private val messageRepository: ChatMessageRepository,
    private val messageSourceService: MessageSourceService
) : MessageHandler {
    override fun handle(sender: AbsSender, message: Message) {
        val chatId = message.chatId.toString()
        val chatUser = chatUserRepository.findByChatIdAndDeletedFalse(message.chatId)!!
        val step = chatUser.step
        val lastMessageId = chatUser.lastMessageId
        val messageId = message.messageId

        val sendMessage = SendMessage()
        sendMessage.enableHtml(true)
        sendMessage.chatId = chatId
        val sendDocument = SendDocument()
        sendDocument.chatId = chatId

        when {
            message.hasText() -> {
                when (val text = message.text) {
                    "/start" -> {
                        when (step) {
                            START -> {
                                sendMessage.replyMarkup = languageKeyboardButton()
                                sendMessage.text = messageSourceService.getMessage(CHOOSE_LANGUAGE_MESSAGE)
                                sender.sendMessage(sendMessage)
                                chatUser.step = CHOOSE_LANG
                                chatUserRepository.save(chatUser)
                            }

                            MAIN_MENU -> {
                                sendMessage.text = messageSourceService.getMessage(MAIN_MENU_MESSAGE)
                                sendMessage.replyMarkup = mainMenu(messageSourceService)
                                chatUser.lastMessageId = sender.sendMessage(sendMessage)
                                sender.deleteMessages(chatId, messageId, lastMessageId)
                                chatUserRepository.save(chatUser)
                            }

                            else -> sender.deleteMessages(chatId, messageId)
                        }
                    }

                    else -> {
                        when (step) {
                            START -> {

                            }

                            CHOOSE_LANG -> {

                            }

                            ENTER_TEXT -> {
                                messageRepository.save(ChatMessage(chatId, messageId, MessageType.TEXT, text))
                                sendMessage.text = messageSourceService.getMessage(MAIN_MENU_MESSAGE)
                                sendMessage.replyMarkup = mainMenu(messageSourceService)
                                chatUser.lastMessageId = sender.sendMessage(sendMessage)
                                sender.deleteMessages(chatId, messageId, lastMessageId)
                                chatUser.step = MAIN_MENU
                                chatUserRepository.save(chatUser)
                            }

                            else -> {
                                sender.deleteMessages(chatId, messageId)
                            }
                        }
                    }
                }
            }

            message.hasContact() -> {
                when (step) {
                    SHARE_CONTACT -> {
                        val contact = message.contact
                        chatUser.phoneNumber = contact.phoneNumber.replace("+", "")

                        sendMessage.text = messageSourceService.getMessage(MAIN_MENU_MESSAGE)
                        sendMessage.replyMarkup = mainMenu(messageSourceService)
                        chatUser.lastMessageId = sender.sendMessage(sendMessage)
                        sender.deleteMessages(chatId, lastMessageId)
                        chatUser.step = MAIN_MENU
                        chatUserRepository.save(chatUser)
                    }
                }
            }

            message.hasDocument() -> {
                //step
                val document = message.document
                messageRepository.save(ChatMessage(chatId, messageId, MessageType.DOCUMENT, fileId = document.fileId))
                sendDocument.document = InputFile(document.fileId)
                sender.sendDocument(sendDocument)
                println(document.fileName)
                File("./documents").mkdirs()
                val file = File("./documents/${Date().time}-${document.fileName}")
                file.writeBytes(
                    getFromTelegram(sender, document.fileId, "5245848431:AAHF1_0SKGFnZHbQJnv4PemHMMTMhftE0Fo")
                )
                sendMessage.text = messageSourceService.getMessage(MAIN_MENU_MESSAGE)
                sendMessage.replyMarkup = mainMenu(messageSourceService)
                chatUser.lastMessageId = sender.sendMessage(sendMessage)
                sender.deleteMessages(chatId, messageId, lastMessageId)
                chatUser.step = MAIN_MENU
                chatUserRepository.save(chatUser)
            }
        }


    }
}