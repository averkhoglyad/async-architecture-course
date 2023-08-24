package io.averkhoglyad.popug.accounting.persistence.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import java.util.*

@Entity
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, insertable = false)
    lateinit var id: UUID

    @Column(updatable = false)
    lateinit var publicId: UUID

    var jiraId: String = ""

    var title: String = ""

    var description: String = ""

    var isActive: Boolean = true
}
