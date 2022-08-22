package uz.demo.demotelegram.bot

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import uz.demo.demotelegram.bot.CallbackType.*
import uz.demo.demotelegram.bot.InlineKeyboardMarkupBuilder.Companion.button
import uz.demo.demotelegram.bot.LocalizationTextKey.*

class InlineKeyboardMarkupBuilder(
    private val inlineKeyboardMarkup: InlineKeyboardMarkup = InlineKeyboardMarkup(),
    private var rows: MutableList<List<InlineKeyboardButton>> = mutableListOf(),
) {

    companion object {
        fun emptyInlineMarkup() = InlineKeyboardMarkupBuilder().build()

        fun button(text: String, callbackData: String) = InlineKeyboardButton().apply {
            this.text = text
            this.callbackData = callbackData
        }

        fun paymentButton(text: String, callbackData: String) = InlineKeyboardButton().apply {
            this.text = text
            this.callbackData = callbackData
            this.pay = true
        }

        fun buttonUrl(text: String, url: String) = InlineKeyboardButton().apply {
            this.text = text
            this.url = url
        }

        fun buttons(map: Map<String, String>): List<InlineKeyboardButton> {//map key is query value is text
            val list = arrayListOf<InlineKeyboardButton>()
            map.map { (key: String, value: String) ->
                {
                    list.add(InlineKeyboardButton().apply {
                        this.text = value
                        this.callbackData = key
                    })
                }
            }
            return list
        }

    }

    fun row(vararg buttons: InlineKeyboardButton): InlineKeyboardMarkupBuilder {
        rows.addAll(listOf(buttons.asList()))
        return this
    }

    fun addRowButton(text: String, callbackData: String): InlineKeyboardMarkupBuilder {
        rows.add(listOf(button(text, callbackData)))
        return this
    }

    fun rowSize(): Int {
        return rows.size
    }

    fun rows(buttons: List<InlineKeyboardButton>): InlineKeyboardMarkupBuilder {
        rows.addAll(buttons.map { listOf(it) })
        return this
    }

    fun build(): InlineKeyboardMarkup {
        inlineKeyboardMarkup.keyboard = rows
        return inlineKeyboardMarkup
    }
}

fun languageKeyboardButton(): InlineKeyboardMarkup {
    val builder = InlineKeyboardMarkupBuilder()
    var row: MutableList<InlineKeyboardButton> = mutableListOf()
    LanguageText.values().forEach {
        row.add(button(it.text, "$CHOOSE_LANGUAGE#${it.name}"))
    }
    builder.row(*row.toTypedArray())
    return builder.build()
}

fun shareContactMessage(messageSourceService: MessageSourceService): ReplyKeyboardMarkup {
    val builder = ReplyKeyboardMarkup.builder()
    builder.keyboard(KeyboardButton(messageSourceService.getMessage(SHARE_CONTACT_BUTTON)).apply {
        requestContact = true
    })
    return builder.disable().build()
}

fun mainMenu(messageSourceService: MessageSourceService): InlineKeyboardMarkup {
    val builder = InlineKeyboardMarkupBuilder()
        .row(
            button(messageSourceService.getMessage(SHARE_TEXT_BUTTON), "$CHOOSE_SHARE_TEXT"),
            button(messageSourceService.getMessage(SHARE_PHOTO_BUTTON), "$CHOOSE_SHARE_PHOTO")
        )
        .rows(
            listOf(
                button(messageSourceService.getMessage(SHARE_AUDIO_BUTTON), "$CHOOSE_SHARE_AUDIO"),
                button(messageSourceService.getMessage(SHARE_DOCUMENT_BUTTON), "$CHOOSE_SHARE_DOCUMENT"),
            )
        )
    return builder.build()
}