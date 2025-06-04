package com.github.saintleva.sourcechew.data.utils

import com.github.saintleva.sourcechew.domain.models.Matching
import com.github.saintleva.sourcechew.domain.models.Parameter
import com.github.saintleva.sourcechew.domain.models.SearchParameter
import com.github.saintleva.sourcechew.domain.models.SearchQuery
import com.github.saintleva.sourcechew.ui.common.utils.ParsingError
import com.github.saintleva.sourcechew.ui.common.utils.parseSearchQuery
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec


class SearchQueryStringConverterTest : FunSpec({

    context("searchQueryToString tests") {

        test("should correctly convert a basic SearchQuery") {
            val query = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("mainValue"), Matching.Contains),
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value1"), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "value2"), Matching.Contains)
                )
            )
            val expected = "mainValue key1=value1 key2value2" // Assuming '=' for Exact, no symbol for Contains
            searchQueryToString(query) shouldBe expected
        }

        test("should correctly convert a SearchQuery with quotes and spaces in values") {
            val query = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("main value with spaces"), Matching.Contains),
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value with \"quotes\""), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "another value\\with\\backslashes"), Matching.Contains)
                )
            )
            val expected = "\"main value with spaces\" key1=\"value with \\\"quotes\\\"\" key2\"another value\\\\with\\\\backslashes\""
            searchQueryToString(query) shouldBe expected
        }

        test("should correctly convert a SearchQuery with tab and newline in values") {
            val query = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("value\twith\ttabs"), Matching.Contains),
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value\nwith\nnewlines"), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "value\rwith\rcarriage\rreturns"), Matching.Contains)
                )
            )
            val expected = "\"value\\twith\\ttabs\" key1=\"value\\nwith\\nnewlines\" key2\"value\\rwith\\rcarriage\\rreturns\""
            searchQueryToString(query) shouldBe expected
        }

        test("should handle a SearchQuery with only main parameter") {
            val query = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("onlyMain"), Matching.Contains),
                parameters = emptyList()
            )
            val expected = "onlyMain"
            searchQueryToString(query) shouldBe expected
        }

        test("should handle a SearchQuery with only named parameters") {
            val query = SearchQuery(
                mainParameter = null,
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value1"), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "value2"), Matching.Contains)
                )
            )
            val expected = " key1=value1 key2value2" // Note the leading space as per your current implementation
            searchQueryToString(query) shouldBe expected
        }

        test("should handle an empty SearchQuery") {
            val query = SearchQuery(
                mainParameter = null,
                parameters = emptyList()
            )
            val expected = ""
            searchQueryToString(query) shouldBe expected
        }

        test("should use correct designation for different Matching types") {
            val query = SearchQuery(
                mainParameter = null,
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value1"), Matching.Exact), // =
                    SearchParameter(Parameter.Pair("key2", "value2"), Matching.Contains), // no symbol
                    SearchParameter(Parameter.Pair("key3", "value3"), Matching.NotEqual) // !=
                    // Add tests for other Matching types if you have them
                )
            )
            // Adjust expected string based on your Matching.designation values
            val expected = " key1${Matching.Exact.designation}value1 key2${Matching.Contains.designation}value2 key3${Matching.NotEqual.designation}value3"
            searchQueryToString(query) shouldBe expected
        }
    }

    context("parseSearchQuery tests") {

        test("should correctly parse a basic search query string") {
            val queryStr = "mainValue key1=value1 key2value2" // Assuming '=' for Exact, no symbol for Contains
            val expected = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("mainValue"), Matching.Contains),
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value1"), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "value2"), Matching.Contains)
                )
            )
            parseSearchQuery(queryStr) shouldBe expected
        }

        test("should correctly parse a search query string with quotes and escaped characters") {
            val queryStr = "\"main value with spaces\" key1=\"value with \\\"quotes\\\"\" key2\"another value\\\\with\\\\backslashes\""
            val expected = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("main value with spaces"), Matching.Contains),
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value with \"quotes\""), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "another value\\with\\backslashes"), Matching.Contains)
                )
            )
            parseSearchQuery(queryStr) shouldBe expected
        }

        test("should correctly parse a search query string with tab and newline in values") {
            val queryStr = "\"value\\twith\\ttabs\" key1=\"value\\nwith\\nnewlines\" key2\"value\\rwith\\rcarriage\\rreturns\""
            val expected = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("value\twith\ttabs"), Matching.Contains),
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value\nwith\nnewlines"), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "value\rwith\rcarriage\rreturns"), Matching.Contains)
                )
            )
            parseSearchQuery(queryStr) shouldBe expected
        }


        test("should handle a search query string with only main parameter") {
            val queryStr = "onlyMain"
            val expected = SearchQuery(
                mainParameter = SearchParameter(Parameter.Main("onlyMain"), Matching.Contains),
                parameters = emptyList()
            )
            parseSearchQuery(queryStr) shouldBe expected
        }

        test("should handle a search query string with only named parameters") {
            val queryStr = " key1=value1 key2value2" // Note the leading space
            val expected = SearchQuery(
                mainParameter = null,
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value1"), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "value2"), Matching.Contains)
                )
            )
            parseSearchQuery(queryStr) shouldBe expected
        }

        test("should handle an empty search query string") {
            val queryStr = ""
            val expected = SearchQuery(
                mainParameter = null,
                parameters = emptyList()
            )
            parseSearchQuery(queryStr) shouldBe expected
        }

        test("should correctly parse different Matching types") {
            // Adjust query string based on your Matching.designation values
            val queryStr = " key1${Matching.Exact.designation}value1 key2${Matching.Contains.designation}value2 key3${Matching.NotEqual.designation}value3"
            val expected = SearchQuery(
                mainParameter = null,
                parameters = listOf(
                    SearchParameter(Parameter.Pair("key1", "value1"), Matching.Exact),
                    SearchParameter(Parameter.Pair("key2", "value2"), Matching.Contains),
                    SearchParameter(Parameter.Pair("key3", "value3"), Matching.NotEqual)
                    // Add tests for other Matching types
                )
            )
            parseSearchQuery(queryStr) shouldBe expected
        }

        // Error handling tests

        test("should throw InvalidFormatException for unclosed quotes") {
            val queryStr = "\"unclosed quotes"
            shouldThrow<ParsingError.InvalidFormatException> {
                parseSearchQuery(queryStr)
            }
        }

        test("should throw InvalidFormatException for key with spaces") {
            val queryStr = "\"main\" key with spaces=value"
            shouldThrow<ParsingError.InvalidFormatException> {
                parseSearchQuery(queryStr)
            }
        }

        test("should throw InvalidFormatException for key with quotes") {
            val queryStr = "\"main\" key\"with\"quotes=value"
            shouldThrow<ParsingError.InvalidFormatException> {
                parseSearchQuery(queryStr)
            }
        }


        test("should throw InvalidFormatException for more than one main parameter") {
            val queryStr = "main1 main2 key=value"
            shouldThrow<ParsingError.InvalidFormatException> {
                parseSearchQuery(queryStr)
            }
        }

        test("should throw UnknownMatchingDesignation for unknown designation") {
            val queryStr = "key@value" // Assuming '@' is not a valid designation
            shouldThrow<ParsingError.UnknownMatchingDesignation> {
                parseSearchQuery(queryStr)
            }
        }

        // Add more error handling tests as needed
    }
})