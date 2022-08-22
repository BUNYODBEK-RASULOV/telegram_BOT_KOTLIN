package uz.demo.demotelegram.bot

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update


class MainBots(
    private val botUsername: String,
    private val token: String,
    private val updateHandlerAdapter: UpdateHandlerAdapter
) : TelegramLongPollingBot() {
    override fun getBotUsername() = botUsername

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update) = updateHandlerAdapter.handle(this, update)
}