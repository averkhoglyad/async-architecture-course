package io.averkhoglyad.popug.tasks.util

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun log4j(): Log4j<Any> = Log4jByInstance

fun log4j(clazz: KClass<*>): Log4j<Any?> = Log4jByClass(clazz.java)

fun log4j(clazz: Class<*>): Log4j<Any?> = Log4jByClass(clazz)

fun log4j(name: String): Log4j<Any?> = Log4jByName(name)

// Log4j
interface Log4j<R> {
    operator fun getValue(thisRef: R, property: KProperty<*>): Logger
}

private object Log4jByInstance : Log4j<Any> {
    override operator fun getValue(thisRef: Any, property: KProperty<*>): Logger = LogManager.getLogger(thisRef)
}

private class Log4jByClass(private val clazz: Class<*>) : Log4j<Any?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger = LogManager.getLogger(clazz)
}

private class Log4jByName(private val name: String) : Log4j<Any?> {
    override operator fun getValue(thisRef: Any?, property: KProperty<*>): Logger = LogManager.getLogger(name)
}