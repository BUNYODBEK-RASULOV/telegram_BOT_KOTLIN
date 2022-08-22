package uz.demo.demotelegram.bot

enum class MessageType {
    TEXT, AUDIO, VIDEO, VIDEO_NOTE, VOICE, DOCUMENT, PHOTO
}

enum class Language(val code: String) {
    //Don't change enum name ask rustam to change
    UZ_LAT("uz_lat"), UZ_CYR("uz_cyr"), RU("ru")//, EN("en")
}

enum class LanguageText(val text: String) {
    //Don't change enum name ask rustam to change
    UZ_LAT("\uD83C\uDDFA\uD83C\uDDFF O'z"),
    UZ_CYR("\uD83C\uDDFA\uD83C\uDDFF Ўз"),
    RU("\uD83C\uDDF7\uD83C\uDDFA Ру"),
//    EN("\uD83C\uDDFA\uD83C\uDDF8 English")
}

enum class Steps {
    START,
    CHOOSE_LANG,
    SHARE_CONTACT,
    MAIN_MENU,
    ENTER_TEXT,
    ENTER_PHOTO,
    ENTER_AUDIO,
    ENTER_DOCUMENT,
}

enum class CallbackType {
    CHOOSE_LANGUAGE,
    CHOOSE_SHARE_TEXT,
    CHOOSE_SHARE_PHOTO,
    CHOOSE_SHARE_AUDIO,
    CHOOSE_SHARE_DOCUMENT,
}

enum class LocalizationTextKey {
    CHOOSE_LANGUAGE_MESSAGE,
    SEND_CONTACT_MESSAGE,
    SHARE_CONTACT_BUTTON,
    SHARE_TEXT_BUTTON,
    SHARE_AUDIO_BUTTON,
    SHARE_PHOTO_BUTTON,
    SHARE_DOCUMENT_BUTTON,
    MAIN_MENU_MESSAGE,
    SEND_TEXT_MESSAGE,
    SEND_PHOTO_MESSAGE,
    SEND_DOCUMENT_MESSAGE,
    SEND_AUDIO_MESSAGE,
}