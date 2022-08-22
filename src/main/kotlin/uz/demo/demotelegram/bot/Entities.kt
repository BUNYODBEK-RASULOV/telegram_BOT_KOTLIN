package uz.demo.demotelegram.bot

import org.hibernate.annotations.ColumnDefault
import javax.persistence.*

@MappedSuperclass
open class BaseEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)

@Entity
class ChatUser(
    val chatId: Long,
    val username: String?,
    var phoneNumber: String? = null,
    var lang: Language = Language.UZ_LAT,
    var step: Steps = Steps.START,
    @ColumnDefault("0") var lastMessageId: Int = 0
) : BaseEntity()

@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["chatId", "messageId"])])
class ChatMessage(
    val chatId: String,
    val messageId: Int,
    val messageType: MessageType,
    val text: String? = null,
    val caption: String? = null,
    val fileId: String? = null
) : BaseEntity()