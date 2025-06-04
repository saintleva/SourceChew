package com.github.saintleva.sourcechew.ui.common.utils

import com.github.saintleva.sourcechew.domain.models.Matching
import com.github.saintleva.sourcechew.domain.models.Parameter
import com.github.saintleva.sourcechew.domain.models.SearchParameter
import com.github.saintleva.sourcechew.domain.models.SearchQuery


// Class to represent parsing errors
sealed class ParsingError(message: String) : Exception(message) {
    class InvalidFormatException(message: String) : ParsingError(message)
    class UnknownMatchingDesignation(designation: String) : ParsingError("Unknown Matching designation: $designation")
    class UnexpectedEndOfInput : ParsingError("Unexpected end of input")
}

// Helper function to decode escaped characters
private fun decodeEscapedCharacters(value: String): String {
    val builder = StringBuilder()
    var i = 0
    while (i < value.length) {
        val char = value[i]
        if (char == '\\' && i + 1 < value.length) {
            when (value[i + 1]) {
                '\\' -> builder.append('\\')
                '"' -> builder.append('"')
                't' -> builder.append('\t')
                'n' -> builder.append('\n')
                'r' -> builder.append('\r')
                else -> {
                    // If it's not a known escape sequence,
                    // just append the backslash and the next character
                    builder.append(char)
                    builder.append(value[i + 1])
                }
            }
            i += 2 // Skip two characters (backslash and escaped character)
        } else {
            builder.append(char)
            i++
        }
    }
    return builder.toString()
}

// Function to get Matching type by its designation
private fun getMatchingByDesignation(designation: String): Matching {
    return Matching.byDesignation[designation] ?: throw ParsingError.UnknownMatchingDesignation(designation)
}

// Function to parse an individual component
private fun parseComponent(component: String): SearchParameter<Parameter> {
    var key: String? = null
    var value: String? = null
    var matching: Matching = Matching.Contains // Default to Contains if no explicit designation

    // Find the Matching designation anywhere in the component (outside quotes)
    var designationPos = -1
    var inQuotes = false
    for (i in component.indices) {
        when (component[i]) {
            '"' -> inQuotes = !inQuotes
            else -> {
                if (!inQuotes) {
                    // Check if a designation starts at the current position
                    Matching.byDesignation.keys.sortedByDescending { it.length }.forEach { designation ->
                        if (component.substring(i).startsWith(designation)) {
                            // Ensure it's not part of a longer designation starting at the same position
                            val isBestStartingAtPos = Matching.byDesignation.keys.none { otherDesignation ->
                                otherDesignation.length > designation.length && component.substring(i).startsWith(otherDesignation)
                            }
                            if(isBestStartingAtPos){
                                designationPos = i
                                matching = getMatchingByDesignation(designation)
                                return@forEach // Found the best matching designation at this position
                            }
                        }
                    }
                    if (designationPos != -1) break // Designation found outside quotes
                }
            }
        }
    }

    if (designationPos != -1) {
        // Designation found outside quotes
        key = component.substring(0, designationPos)
        value = component.substring(designationPos + matching.designation.length)

        // **New check:** Key cannot contain spaces or quotes
        if (key.contains(" ") || key.contains("\"")) {
            throw ParsingError.InvalidFormatException("Parameter key cannot contain spaces or quotes: '$key'")
        }

    } else {
        // Designation not found outside quotes, it's either a main parameter,
        // or a named parameter without an explicit designation (Contains)
        value = component
    }

    // Process quotes and escaped characters in the value
    val processedValue = value?.let {
        if (it.startsWith("\"") && it.endsWith("\"")) {
            decodeEscapedCharacters(it.substring(1, it.length - 1))
        } else {
            it
        }
    }

    // If a key exists, it's a Parameter.Pair
    if (key != null) {
        // If no explicit designation was found in the string (designationPos == -1),
        // but a key exists, it's a Parameter.Pair with Matching.Contains
        if (designationPos == -1) {
            matching = Matching.Contains
        }
        return SearchParameter(Parameter.Pair(key, processedValue ?: ""), matching)
    } else {
        // If no key, it's a Parameter.Main.
        // If a designation was found, use it. Otherwise, default to Matching.Contains.
        return SearchParameter(Parameter.Main(processedValue ?: ""), matching)
    }
}

fun parseSearchQuery(queryStr: String): SearchQuery {
    val components = mutableListOf<String>()
    var currentComponent = StringBuilder()
    var inQuotes = false

    for (i in queryStr.indices) {
        when (queryStr[i]) {
            '"' -> {
                inQuotes = !inQuotes
                currentComponent.append(queryStr[i])
            }
            ' ' -> {
                if (inQuotes) {
                    currentComponent.append(queryStr[i])
                } else {
                    if (currentComponent.isNotEmpty()) {
                        components.add(currentComponent.toString())
                        currentComponent = StringBuilder()
                    }
                }
            }
            else -> {
                currentComponent.append(queryStr[i])
            }
        }
    }

    // Check if quotes are closed at the end of the string
    if (inQuotes) {
        throw ParsingError.InvalidFormatException("Unclosed quotes at the end of the string")
    }

    // Add the last component
    if (currentComponent.isNotEmpty()) {
        components.add(currentComponent.toString())
    }

    var mainParameter: SearchParameter<Parameter.Main>? = null
    val parameters = mutableListOf<SearchParameter<Parameter.Pair>>()

    for (component in components) {
        try {
            val parsed = parseComponent(component)
            when (parsed.parameter) {
                is Parameter.Main -> {
                    if (mainParameter == null) {
                        mainParameter = parsed as SearchParameter<Parameter.Main>
                    } else {
                        // If a second main parameter is found
                        throw ParsingError.InvalidFormatException("Found more than one main parameter")
                    }
                }
                is Parameter.Pair -> {
                    val pairParameter = parsed.parameter as Parameter.Pair
                    parameters.add(SearchParameter(pairParameter, parsed.matching))
                }
            }
        } catch (e: ParsingError) {
            // Wrap component parsing errors to provide more context
            throw ParsingError.InvalidFormatException("Error parsing component '$component': ${e.message}")
        }
    }

    return SearchQuery(mainParameter, parameters)