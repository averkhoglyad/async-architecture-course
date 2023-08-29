package io.averkhoglyad.popug.accounting.core.persistence.entity

import jakarta.persistence.*
import java.util.*

@Entity
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, insertable = false)
    lateinit var id: UUID

    @Column(updatable = false)
    lateinit var publicId: UUID

    var title: String = ""

    var description: String = ""

    var isActive: Boolean = true
}
