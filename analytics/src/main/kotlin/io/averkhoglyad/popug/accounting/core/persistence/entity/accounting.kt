package io.averkhoglyad.popug.accounting.core.persistence.entity

import jakarta.persistence.*
import org.hibernate.annotations.Immutable
import java.time.Instant
import java.util.*
import kotlin.reflect.KClass

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

    open lateinit var publicId: UUID

    open var amount: Int = 0

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    open lateinit var user: UserEntity

    open var occurredAt: Instant = Instant.now()

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