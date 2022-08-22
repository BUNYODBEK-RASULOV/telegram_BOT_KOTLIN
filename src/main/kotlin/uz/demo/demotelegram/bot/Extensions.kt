package uz.demo.demotelegram.bot

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.UnpinChatMessage
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File

fun Update.getTelegramUsername(): String? = this.run {
    when {
        hasInlineQuery() -> inlineQuery.from.userName
        hasChosenInlineQuery() -> chosenInlineQuery.from.userName
        hasCallbackQuery() -> callbackQuery.from.userName
        hasPreCheckoutQuery() -> preCheckoutQuery.from.userName
        hasMyChatMember() -> myChatMember.from.userName
        else -> message.from.userName
    }
}

fun toLink(title: String, url: String) = "<a href=\"$url\">$title</a>"

fun ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder.keyboard(keyboardButton: KeyboardButton) {
    this.keyboardRow(KeyboardRow(mutableListOf(keyboardButton)))
}

fun Update.getTelegramId() = this.run {
    when {
        hasInlineQuery() -> inlineQuery.from.id
        hasChosenInlineQuery() -> chosenInlineQuery.from.id
        hasCallbackQuery() -> callbackQuery.from.id
        hasPreCheckoutQuery() -> preCheckoutQuery.from.id
        hasMyChatMember() -> myChatMember.from.id
        else -> message.from.id
    }
}

fun AbsSender.pinMessage(chatId: String, messageId: Int) {
    try {
        val pinMessage = PinChatMessage(chatId, messageId)
        this.execute(pinMessage)
    } catch (e: Exception) {
        println("Could not pin message from chat $chatId, message $messageId")
    }
}

fun AbsSender.unpinMessage(chatId: String, messageId: Int) {
    try {
        this.execute(UnpinChatMessage(chatId, messageId))
    } catch (e: Exception) {
        println("Could not unpin message from chat $chatId, message $messageId. Error message: ${e.message}")
    }
}

fun AbsSender.deleteMessages(chatId: String, vararg messageIds: Int) {
    messageIds.forEach { this.deleteMessages(chatId, it) }
}

fun AbsSender.deleteMessages(chatId: String, messageId: Int) {
    val result = DeleteMessage()
    result.chatId = chatId
    result.messageId = messageId
    try {
        this@deleteMessages.executeAsync(result)
    } catch (e: Exception) {
        this@deleteMessages.editKeyboard(EditMessageReplyMarkup().apply {
            this.chatId = chatId
            this.messageId = messageId
            this.replyMarkup = InlineKeyboardMarkupBuilder.emptyInlineMarkup()
        })
    }
}

fun AbsSender.deleteMessageList(chatId: String, messageIdList: List<Int>) {
    messageIdList.forEach {
        this.deleteMessages(chatId, it)
    }
}

fun AbsSender.removeInlineMarkup(messageId: Int, chatId: String) {
    val result = EditMessageReplyMarkup()
    result.chatId = chatId
    result.messageId = messageId

    result.replyMarkup = InlineKeyboardMarkupBuilder().build()

    try {
        this.executeAsync(result)
    } catch (e: TelegramApiException) {
        println("Error editing message")
    }
}

fun AbsSender.deleteMessages(result: DeleteMessage) {
    try {
        this.executeAsync(result)
    } catch (e: TelegramApiException) {
        println("Error deleting message")
    }
}

fun AbsSender.deleteInlineKeyboard(messageId: Int, chatId: String) {
    val editKeyboard = EditMessageReplyMarkup()
    editKeyboard.messageId = messageId
    editKeyboard.chatId = chatId
    editKeyboard.replyMarkup = InlineKeyboardMarkupBuilder.emptyInlineMarkup()
    try {
        this.execute(editKeyboard)
    } catch (e: TelegramApiException) {
        //TODO: Delete qilinmadi
    }
}

//fun AbsSender.setBotCommands(chatId: String, messageSourceService: MessageSourceService) {
//    val setMyCommands = SetMyCommands()
//
//    val commands = mutableListOf<BotCommand>()
//    BotCommands.values().forEach {
//        commands.add(BotCommand(it.command, messageSourceService.getMessage(it.localizationTextKey)))
//    }
//    val scope = BotCommandScopeChat(chatId)
//    setMyCommands.scope = scope
//    setMyCommands.commands = commands
//    try {
//        GlobalScope.async {
//            delay(500)
//            execute(setMyCommands)
//        }
//    } catch (e: Exception) {
//        println("setBotCommands: " + e.message)
//    }
//}

fun AbsSender.editMessage(editMessageMedia: EditMessageMedia) {
    try {
        this.execute(editMessageMedia)
    } catch (e: TelegramApiException) {
//        println("Couldn't edit message media. messageId = ${editMessageMedia.messageId}, chatId = ${editMessageMedia.chatId}. Error Message: ${e.message}")
    }
}

fun AbsSender.editMessage(editMessageMedia: EditMessageMedia, file: File?) {
    try {
        this.execute(editMessageMedia)
    } catch (e: TelegramApiException) {
//        println("Couldn't edit message media. messageId = ${editMessageMedia.messageId}, chatId = ${editMessageMedia.chatId}. Error Message: ${e.message}")
    } finally {
        file?.delete()
    }
}

fun AbsSender.editMessage(editMessage: EditMessageText) {
    try {
        this.execute(editMessage)
    } catch (e: TelegramApiException) {
        println("Couldn't edit message. messageId = ${editMessage.messageId}, chatId = ${editMessage.chatId}. Error Message: ${e.message}")
    }
}

fun AbsSender.editKeyboard(editMessageReplyMarkup: EditMessageReplyMarkup) {
    try {
        this.executeAsync(editMessageReplyMarkup)
    } catch (e: TelegramApiException) {
        println("Couldn't edit message. messageId = ${editMessageReplyMarkup.messageId}, chatId = ${editMessageReplyMarkup.chatId}. Error Message: ${e.message}")
    }
}

fun AbsSender.editCaption(editMessageCaption: EditMessageCaption) {
    try {
        this.executeAsync(editMessageCaption)
    } catch (e: TelegramApiException) {
//        println("Couldn't edit message caption. messageId = ${editMessageCaption.messageId}, chatId = ${editMessageCaption.chatId}. Error Message: ${e.message}")
    }
}

fun AbsSender.sendInvoice(send: SendInvoice): Int? {
    try {
        return this.execute(send).messageId
    } catch (e: Exception) {
        println("Couldn't send invoice to chat(${send.chatId}). Message: ${e}")
    }
    return null
}

fun AbsSender.sendMessage(send: SendMessage): Int {
    return this.execute(send).messageId
}

fun AbsSender.sendMessage(chatId: String, text: String): Int? {
    return this.execute(SendMessage(chatId, text)).messageId
}

fun AbsSender.sendDocument(sendDocument: SendDocument): Int? {
    return execute(sendDocument).messageId
}

fun AbsSender.sendPhoto(sendPhoto: SendPhoto): Int {
    return execute(sendPhoto).messageId
}

fun AbsSender.answerCallBackQuery(answerCallbackQuery: AnswerCallbackQuery) {
    try {
        answerCallbackQuery.cacheTime = 2
        this.execute(answerCallbackQuery)
    } catch (e: TelegramApiException) {
        println(e.message)
    }
}

fun AbsSender.deleteMessages(message: Message, lastMessageId: Int, cache: String) {
    val chatId = message.chatId.toString()
    cache.toIntOrNull()?.run {
        this@deleteMessages.deleteMessages(chatId, this)
    }
    if (lastMessageId != 0) {
        this.deleteMessages(chatId, lastMessageId)
    }
    this.deleteMessages(chatId, message.messageId)
}

fun ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder.disable(): ReplyKeyboardMarkup.ReplyKeyboardMarkupBuilder {
    this.resizeKeyboard(true)
    this.oneTimeKeyboard(false)
    this.selective(true)
    return this
}