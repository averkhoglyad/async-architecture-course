package io.averkhoglyad.popug.accounting.persistence.repository

import io.averkhoglyad.popug.accounting.persistence.entity.UserAccountOperation
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserAccountOperationRepository : JpaRepository<UserAccountOperation, UUID> {



}
