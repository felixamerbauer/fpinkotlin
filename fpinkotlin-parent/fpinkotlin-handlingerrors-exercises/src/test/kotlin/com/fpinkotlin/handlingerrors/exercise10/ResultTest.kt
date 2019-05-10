package com.fpinkotlin.handlingerrors.exercise10


import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec

class ResultTest : StringSpec() {

    init {

        "forEachSuccessFailure" {
            forAll { z: Int ->
                val errorMessage = "Value is odd"
                var result = false
                Result(if (z % 2 == 0) z else null, errorMessage).forEachOrElse({ x -> result = (x == z) }, { e ->
                    result = (e
                            .message
                            == errorMessage)
                }, {})
                result
            }
        }

        "forEachSuccessEmpty" {
            forAll { z: Int ->
                var result = false
                (if (z % 2 == 0) Result(z) else Result()).forEachOrElse({ x -> result = (x == z) }, {}, { result = true })
                result
            }
        }
    }
}
