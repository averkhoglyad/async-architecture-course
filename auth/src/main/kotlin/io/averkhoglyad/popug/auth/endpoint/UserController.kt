package io.averkhoglyad.popug.auth.endpoint

import io.averkhoglyad.popug.auth.entity.UserEntity
import io.averkhoglyad.popug.auth.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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