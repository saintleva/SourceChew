package com.github.saintleva.sourcechew.domain.models


//TODO: remove this
//enum class SearchType {
//    Repo,
//    User,
//    Group
//}

sealed interface MainParameterSupporting {
    data object Supported: MainParameterSupporting
    data class MatchingNotAllowed(val matching: Matching): MainParameterSupporting
}

sealed interface ParameterSupporting {
    data object Supported: ParameterSupporting
    data class ParameterNotAllowed(val parameter: String): ParameterSupporting
    data class MatchingNotAllowed(
        val parameter: String,
        val matching: Matching): ParameterSupporting
}

class SearchRules(
    private val allowedMainParameter: Set<Matching>,
    private val allowedParameters: Map<String, Set<Matching>>
) {
    fun validateMain(matching: Matching): MainParameterSupporting {
        return if (matching in allowedMainParameter) MainParameterSupporting.Supported
        else MainParameterSupporting.MatchingNotAllowed(matching)
    }

    fun validate(parameter: String, matching: Matching): ParameterSupporting {
        if (!allowedParameters.containsKey(parameter))
            return ParameterSupporting.ParameterNotAllowed(parameter)
        if (matching !in allowedParameters[parameter]!!)
            return ParameterSupporting.MatchingNotAllowed(parameter, matching)
        return ParameterSupporting.Supported
    }
}

class ForgeSearchRules(
    val repo: SearchRules,
    val user: SearchRules,
    val group: SearchRules
)