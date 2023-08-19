package io.averkhoglyad.popug.tasks.persistence.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.*

@Entity
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, insertable = false)
    var id: UUID? = null
    @Column(updatable = false)
    lateinit var publicId: UUID

    @NotBlank
    var title: String = ""
    var description: String = ""

    @get:JsonProperty
    @set:JsonIgnore
    var status: TaskStatus = TaskStatus.OPEN

    @Positive
    @get:JsonProperty
    @set:JsonIgnore
    var userCost: Int = 0

    @Positive
    @get:JsonProperty
    @set:JsonIgnore
    var userRevenue: Int = 0

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "assignee_id")
    @get:JsonProperty
    @set:JsonIgnore
    var assignee: UserEntity? = null
}

enum class TaskStatus {
    OPEN,
    CLOSED
}
