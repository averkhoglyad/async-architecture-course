package io.averkhoglyad.popug.auth.service

import io.averkhoglyad.popug.auth.repository.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DbAwareUserDetailsService(private val repo: UserRepository) : UserDetailsService {

    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = repo.findByLogin(username)
            .orElseThrow { UsernameNotFoundException("Username $username not found") }
        return User.builder()
            .username(user.login)
            .roles(user.role.toString())
            .password(user.passwordHash)
            .build()
    }
}