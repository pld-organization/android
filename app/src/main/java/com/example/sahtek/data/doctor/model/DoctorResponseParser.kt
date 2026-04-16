package com.example.sahtek.data.doctor.model

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

internal object DoctorResponseParser {
    private val arrayKeys = listOf("doctors", "data", "results", "items")

    fun parseDoctors(root: JsonElement?): List<DoctorDto> {
        val doctorArray = findDoctorArray(root) ?: return emptyList()

        return doctorArray.mapNotNull { element ->
            when {
                element.isJsonObject -> DoctorDto(
                    id = extractString(element.asJsonObject, "_id", "id"),
                    displayName = extractString(element.asJsonObject, "name", "fullName", "doctorName").ifBlank { null },
                    firstName = extractString(element.asJsonObject, "firstName", "firstname").ifBlank { null },
                    lastName = extractString(element.asJsonObject, "lastName", "lastname").ifBlank { null },
                    speciality = extractString(element.asJsonObject, "speciality", "specialty").ifBlank { null },
                    establishment = extractString(element.asJsonObject, "establishment").ifBlank { null },
                    email = extractString(element.asJsonObject, "email").ifBlank { null }
                )
                element.isJsonPrimitive && element.asJsonPrimitive.isString -> {
                    DoctorDto(id = element.asString)
                }
                else -> null
            }
        }
    }

    fun parseDoctor(root: JsonElement?, fallbackId: String = ""): DoctorDto? {
        if (root == null || root.isJsonNull) return null

        return when {
            root.isJsonObject -> parseDoctorObject(root.asJsonObject, fallbackId)
            root.isJsonArray -> root.asJsonArray
                .firstOrNull()
                ?.let { parseDoctor(it, fallbackId) }
            root.isJsonPrimitive && root.asJsonPrimitive.isString -> DoctorDto(id = root.asString.ifBlank { fallbackId })
            else -> null
        }
    }

    private fun findDoctorArray(element: JsonElement?): JsonArray? {
        if (element == null || element.isJsonNull) return null

        return when {
            element.isJsonArray -> element.asJsonArray
            element.isJsonObject -> findDoctorArrayInObject(element.asJsonObject)
            else -> null
        }
    }

    private fun findDoctorArrayInObject(obj: JsonObject): JsonArray? {
        arrayKeys.forEach { key ->
            val value = obj.get(key)
            val directArray = findDoctorArray(value)
            if (directArray != null) return directArray
        }

        obj.entrySet().forEach { (_, value) ->
            val nestedArray = findDoctorArray(value)
            if (nestedArray != null && looksLikeDoctorArray(nestedArray)) {
                return nestedArray
            }
        }

        return null
    }

    private fun looksLikeDoctorArray(array: JsonArray): Boolean {
        if (array.size() == 0) return true

        return array.all { element ->
            when {
                element.isJsonObject -> {
                    val obj = element.asJsonObject
                    obj.has("_id") || obj.has("id") || obj.has("name") || obj.has("fullName") ||
                        obj.has("firstName") || obj.has("lastname") || obj.has("lastName") ||
                        obj.has("speciality") || obj.has("specialty")
                }
                element.isJsonPrimitive -> element.asJsonPrimitive.isString
                else -> false
            }
        }
    }

    private fun parseDoctorObject(obj: JsonObject, fallbackId: String): DoctorDto? {
        val directDoctor = toDoctorDto(obj, fallbackId)
        if (directDoctor.id.isNotBlank() || directDoctor.hasHumanName) {
            return directDoctor
        }

        val nestedKeys = listOf("doctor", "data", "profile", "user", "result")
        nestedKeys.forEach { key ->
            val nested = obj.get(key)
            if (nested != null) {
                val parsed = parseDoctor(nested, fallbackId)
                if (parsed != null && (parsed.id.isNotBlank() || parsed.hasHumanName)) {
                    return parsed
                }
            }
        }

        obj.entrySet().forEach { (_, value) ->
            val parsed = parseDoctor(value, fallbackId)
            if (parsed != null && (parsed.id.isNotBlank() || parsed.hasHumanName)) {
                return parsed
            }
        }

        return null
    }

    private fun toDoctorDto(obj: JsonObject, fallbackId: String): DoctorDto {
        val nestedDoctor = obj.getAsJsonObjectOrNull("doctor")
        val nestedUser = obj.getAsJsonObjectOrNull("user")
        val nestedProfile = obj.getAsJsonObjectOrNull("profile")

        return DoctorDto(
            id = firstNonBlank(
                extractString(obj, "_id", "id"),
                extractString(nestedDoctor, "_id", "id"),
                extractString(nestedUser, "_id", "id"),
                extractString(nestedProfile, "_id", "id"),
                fallbackId
            ),
            displayName = firstNonBlank(
                extractString(obj, "name", "fullName", "doctorName"),
                extractString(nestedDoctor, "name", "fullName", "doctorName"),
                extractString(nestedUser, "name", "fullName"),
                extractString(nestedProfile, "name", "fullName")
            ).ifBlank { null },
            firstName = firstNonBlank(
                extractString(obj, "firstName", "firstname"),
                extractString(nestedDoctor, "firstName", "firstname"),
                extractString(nestedUser, "firstName", "firstname"),
                extractString(nestedProfile, "firstName", "firstname")
            ).ifBlank { null },
            lastName = firstNonBlank(
                extractString(obj, "lastName", "lastname"),
                extractString(nestedDoctor, "lastName", "lastname"),
                extractString(nestedUser, "lastName", "lastname"),
                extractString(nestedProfile, "lastName", "lastname")
            ).ifBlank { null },
            speciality = firstNonBlank(
                extractString(obj, "speciality", "specialty"),
                extractString(nestedDoctor, "speciality", "specialty"),
                extractString(nestedProfile, "speciality", "specialty")
            ).ifBlank { null },
            establishment = firstNonBlank(
                extractString(obj, "establishment"),
                extractString(nestedDoctor, "establishment"),
                extractString(nestedProfile, "establishment")
            ).ifBlank { null },
            email = firstNonBlank(
                extractString(obj, "email"),
                extractString(nestedDoctor, "email"),
                extractString(nestedUser, "email"),
                extractString(nestedProfile, "email")
            ).ifBlank { null }
        )
    }

    private fun extractString(obj: JsonObject?, vararg keys: String): String {
        if (obj == null) return ""

        keys.forEach { key ->
            val value = obj.get(key)
            if (value != null && value.isJsonPrimitive && value.asJsonPrimitive.isString) {
                return value.asString.trim()
            }
        }

        return ""
    }

    private fun firstNonBlank(vararg values: String): String =
        values.firstOrNull { it.isNotBlank() }.orEmpty()

    private fun JsonObject.getAsJsonObjectOrNull(key: String): JsonObject? {
        val value = get(key)
        return if (value != null && value.isJsonObject) value.asJsonObject else null
    }
}
