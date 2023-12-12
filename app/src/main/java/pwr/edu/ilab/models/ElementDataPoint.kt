package pwr.edu.ilab.models

import java.util.Optional

data class ElementDataPoint(
    val result: Number,
    val unit: String,
    val date: String,
) {
    companion object {
        fun fromSingleTestResults(
            testResults: SingleTestResults,
            elementName: String
        ): Optional<ElementDataPoint> {
            val elementResult = testResults.results.find { elementResult ->
                elementResult.name == elementName
            }
            if (elementResult != null) {
                return Optional.of(
                    ElementDataPoint(
                        elementResult.result,
                        elementResult.unit,
                        testResults.date,
                    )
                )
            }

            return Optional.empty()
        }
    }
}