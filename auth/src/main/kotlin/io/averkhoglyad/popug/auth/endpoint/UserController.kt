package io.averkhoglyad.popug.auth.endpoint

import io.averkhoglyad.popug.auth.core.entity.UserEntity
import io.averkhoglyad.popug.auth.core.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(private val service: UserService) {

    @GetMapping
    fun list(pageable: Pageable): Page<UserEntity> {
        require(pageable.isPaged)
        return service.findList(pageable)
    }

    @GetMapping("/{id}")
    fun details(@PathVariable id: UUID): UserEntity {
        return service.findEntity(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody user: UserEntity): UserEntity {
        user.id = null
        return service.save(user)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: UUID, @RequestBody user: UserEntity): UserEntity {
        user.id = id
        return service.save(user)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        service.delete(id)
    }
}