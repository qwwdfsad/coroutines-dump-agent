package org.qwwdfsad

import sun.jvm.hotspot.oops.*
import sun.jvm.hotspot.runtime.*
import sun.jvm.hotspot.tools.*

class CoroutineDump : Tool() {

    private lateinit var coroutineKlass: Klass
    private var traces = ArrayList<ArrayList<String>>()

    override fun run() {

        val vm = VM.getVM()
        vm.systemDictionary.classesDo { klass, loader ->
            val klassName = klass.name.asString()
            if (klassName == "kotlin/coroutines/experimental/jvm/internal/CoroutineImpl") {
                coroutineKlass = klass
            }
        }


        vm.objectHeap.iterateObjectsOfKlass(object : DefaultHeapVisitor() {
            override fun doObj(oop: Oop): Boolean {
                printStackTrace(oop)
                println()
                return false
            }
        }, coroutineKlass)

        // TODO filter out nested coroutines
        var i = 0
        traces.forEach { trace ->
            println("Stacktrace [${++i}]")
            trace.forEach { println(it) }
            println()
        }
    }

    fun execute(pid: Int) {
        execute(arrayOf(pid.toString()))
    }

    private fun printStackTrace(oop: Oop) {
        val trace = ArrayList<String>()
        trace += oop.klass.name.asString()
        printCompletion(oop, trace)
        traces.add(trace)
    }

    private tailrec fun printCompletion(oop: Oop, trace: ArrayList<String>) {
        val symbols = VM.getVM().symbolTable
        val nameSym = symbols.probe("completion")
        val sigSym = symbols.probe("Lkotlin/coroutines/experimental/Continuation;")
        val field = (coroutineKlass as InstanceKlass).findField(nameSym, sigSym) as OopField
        val completion = field.getValue(oop) ?: return
        val value = completion.klass.name.asString()
        trace += value
        printCompletion(completion, trace)
    }
}

fun main(args: Array<String>) {
    CoroutineDump().execute(args[0].toIntOrNull() ?: error("First argument should be a valid PID"))
}
