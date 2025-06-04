package com.github.saintleva.sourcechew.data.utils

import com.github.saintleva.sourcechew.domain.models.Matching
import com.github.saintleva.sourcechew.domain.models.Parameter
import com.github.saintleva.sourcechew.domain.models.SearchParameter
import com.github.saintleva.sourcechew.domain.models.SearchQuery


private fun needsQuoting(value: String): Boolean {
    return value.contains(" ") || value.contains("\"") || value.contains("\\") ||
           value.contains("\t") || value.contains("\n") || value.contains("\r")
}

private fun escapeDangerousCharacters(value: String): String {
    return value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\t", "\\t")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
}

private fun parameterToString(searchParameter: SearchParameter<Parameter>): String {
    val param = searchParameter.parameter
    val formattedValue = if (needsQuoting(param.value)) {
        "\"${escapeDangerousCharacters(param.value)}\""
    } else {
        param.value
    }
    return when (param) {
        is Parameter.Main -> {
            if (searchParameter.matching is Matching.Contains) {
                "$formattedValue"
            } else {
                "${searchParameter.matching.designation}:$formattedValue"
            }
        }
        is Parameter.Pair -> {
            "${param.key}${searchParameter.matching.designation}$formattedValue"
        }
    }
}

fun searchQueryToString(query: SearchQuery): String {
    val builder = StringBuilder()
    if (query.mainParameter != null) {
        builder.append(parameterToString(query.mainParameter))
    }
    for (parameter in query.parameters) {
        builder.append(" ")
        builder.append(parameterToString(parameter))
    }
    return builder.toString()
}