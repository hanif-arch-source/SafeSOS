package com.example.safesos.util

import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Coursework-level password hashing: a random per-user salt + SHA-256.
 *
 * This is NOT production security (no bcrypt/Argon2, no server-side
 * verification) — it's disclosed as such in SafeSOS_Build_Log.md. It is,
 * however, a real improvement over storing plaintext or an unsalted hash:
 * two users with the same password get different stored hashes, and the
 * salt is stored alongside the hash so it never needs to be guessed.
 */
object PasswordUtil {

    /** Generates a new random salt, hex-encoded, for a newly registered user. */
    fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /** Hashes [password] with [salt] using SHA-256, returning a hex string. */
    fun hash(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        digest.update(salt.toByteArray(Charsets.UTF_8))
        val hashed = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashed.joinToString("") { "%02x".format(it) }
    }

    /** Verifies [password] against a previously stored [salt]/[expectedHash] pair. */
    fun verify(password: String, salt: String, expectedHash: String): Boolean {
        return hash(password, salt) == expectedHash
    }
}
