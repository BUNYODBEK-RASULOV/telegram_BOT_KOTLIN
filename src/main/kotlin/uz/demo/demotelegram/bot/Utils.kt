package uz.demo.demotelegram.bot

import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.bots.AbsSender

fun getFromTelegram(sender: AbsSender, fileId: String, token: String) = sender.execute(GetFile(fileId)).run {
    RestTemplate().getForObject<ByteArray>("https://api.telegram.org/file/bot${token}/${filePath}")
}