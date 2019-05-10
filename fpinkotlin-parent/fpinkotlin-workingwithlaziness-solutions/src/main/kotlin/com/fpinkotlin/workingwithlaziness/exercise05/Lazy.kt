package com.fpinkotlin.workingwithlaziness.exercise05


class Lazy<out A>(function: () -> A) : () -> A {

    private val value: A by lazy(function)

    override operator fun invoke(): A = value
}

fun <A, B, C> lift2(f: (A) -> (B) -> C): (Lazy<A>) -> (Lazy<B>) -> Lazy<C> =
        { ls1 ->
            { ls2 ->
                Lazy { f(ls1())(ls2()) }
            }
        }

val consMessage: (String) -> (String) -> String =
        { greetings ->
            { name ->
                "$greetings, $name!"
            }
        }
