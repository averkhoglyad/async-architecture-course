package io.averkhoglyad.popug.tasks.entity

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import jakarta.validation.constraints.Negative
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.util.*

class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
    var title: String = ""
    var description: String = ""
    @set:JsonIgnore
    var status: TaskStatus = TaskStatus.OPEN
    @Negative
    var userCost: Int = 0
    @Positive
    var userRevenue: Int = 0
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "assignee_id")
    var assignee: User? = null
}

enum class TaskStatus {
    OPEN,
    IN_PROGRESS, //???
    CLOSED
}
