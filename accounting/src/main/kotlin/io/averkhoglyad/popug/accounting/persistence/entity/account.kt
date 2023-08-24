package io.averkhoglyad.popug.accounting.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

@Entity
class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, insertable = false)
    lateinit var id: UUID
    @Column(updatable = false)
    var publicId: UUID = UUID.randomUUID()

    var balance: Int = 0

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", updatable = false)
    @get:JsonProperty
    @set:JsonIgnore
    lateinit var owner: UserEntity

}

@Immutable
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("-")
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.CHAR)
abstract class UserAccountOperation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open lateinit var id: UUID
        protected set

    open var publicId: UUID = UUID.randomUUID()
        protected set

    open var amount: Int = 0

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_account_id")
    open lateinit var userAccount: UserAccount

    open var occurredAt: Instant = Instant.now()
        protected set

    @get:Transient
    abstract val type: KClass<out UserAccountOperation>
}

@Entity
@DiscriminatorValue("t")
class UserAccountTaskOperation : UserAccountOperation() {

    @ManyToOne(optional = true)
    @JoinColumn(name = "task_id")
    var task: Task? = null

    @Transient
    override val type = UserAccountTaskOperation::class
}

@Entity
@DiscriminatorValue("w")
class UserAccountWithdrawOperation : UserAccountOperation() {

    @Transient
    override val type = UserAccountWithdrawOperation::class
}