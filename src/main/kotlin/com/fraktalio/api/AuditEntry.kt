package com.fraktalio.api

import com.fraktalio.restaurant.command.api.ANONYMOUS
import java.util.*

data class AuditEntry(
    val who: String = ANONYMOUS,
    val `when`: Date = Calendar.getInstance().time,
    val auth: Collection<String> = listOf(ANONYMOUS)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AuditEntry

        if (who != other.who) return false
        if (auth != other.auth) return false

        return true
    }

    override fun hashCode(): Int {
        var result = who.hashCode()
        result = 31 * result + auth.hashCode()
        return result
    }
}
