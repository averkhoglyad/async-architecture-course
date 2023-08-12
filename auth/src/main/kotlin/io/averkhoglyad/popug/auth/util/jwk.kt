package io.averkhoglyad.popug.auth.util

import com.nimbusds.jose.jwk.RSAKey
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*


fun generateRsa(): RSAKey {
    val keyPair = generateRsaKey()
    val publicKey = keyPair.public as RSAPublicKey
    val privateKey = keyPair.private as RSAPrivateKey
    return RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .keyID(UUID.randomUUID().toString())
        .build()
}

fun generateRsaKey(): KeyPair {
    try {
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        return keyPairGenerator.generateKeyPair()
    } catch (ex: Exception) {
        throw IllegalStateException(ex)
    }
}