package com.fpinkotlin.workingwithlaziness.exercise10

import com.fpinkotlin.common.List
import com.fpinkotlin.common.Result
import com.fpinkotlin.common.sequence

class Lazy<out A>(function: () -> A) : () -> A {

    private val value: A by lazy(function)

    override operator fun invoke(): A = value

    fun <B> map(f: (A) -> B): Lazy<B> = Lazy { f(value) }

    fun <B> flatMap(f: (A) -> Lazy<B>): Lazy<B> = Lazy { f(value)() }

    fun forEach(condition: Boolean, ifTrue: (A) -> Unit, ifFalse: () -> Unit = {}): Unit = TODO("forEach")

    fun forEach(condition: Boolean, ifTrue: () -> Unit = {}, ifFalse: (A) -> Unit): Unit = TODO("forEach")

    fun forEach(condition: Boolean, ifTrue: (A) -> Unit, ifFalse: (A) -> Unit): Unit = TODO("forEach")
}

fun <A, B, C> lift2(f: (A) -> (B) -> C): (Lazy<A>) -> (Lazy<B>) -> Lazy<C> =
        { ls1 ->
            { ls2 ->
                Lazy { f(ls1())(ls2()) }
            }
        }

fun <A> sequence(lst: List<Lazy<A>>): Lazy<List<A>> = Lazy { lst.map { it() } }

fun <A> sequenceResult(lst: List<Lazy<A>>): Lazy<Result<List<A>>> =
        Lazy { sequence(lst.map { Result.of(it) }) }
