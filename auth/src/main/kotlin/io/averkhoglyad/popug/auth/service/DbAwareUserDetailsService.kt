package io.averkhoglyad.popug.auth.service

import io.averkhoglyad.popug.auth.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DbAwareUserDetailsService(private val repository: UserRepository) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = repository.findByLogin(username)
            .orElseThrow { UsernameNotFoundException("Username $username not found") }
        return User.builder()
            .username(user.id.toString())
            .roles(user.role.toString())
            .password(user.passwordHash)
            .build()
    }
}