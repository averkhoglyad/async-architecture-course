package io.averkhoglyad.popug.accounting.config

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.TypeFactory
import io.averkhoglyad.popug.accounting.core.event.business.task.*
import io.averkhoglyad.popug.accounting.core.event.business.task.TaskLifecycleEvent.*
import io.averkhoglyad.popug.accounting.core.event.streaming.task.TaskDto
import io.averkhoglyad.popug.accounting.core.event.streaming.user.UserDto
import io.averkhoglyad.popug.common.kafka.PopugKafkaHeaders
import io.averkhoglyad.popug.common.kafka.getLastAsString
import io.averkhoglyad.popug.schema.JsonSchemaValidator
import io.averkhoglyad.popug.schema.SchemaValidator
import io.averkhoglyad.popug.schema.kafka.SchemaValidationDeserializer
import io.averkhoglyad.popug.schema.kafka.SchemaValidationSerializer
import org.apache.kafka.common.header.Headers
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.ContainerCustomizer
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

const val KAFKA_LISTENER_STREAMING_TASK = "KAFKA_LISTENER_STREAMING_TASK"
const val KAFKA_LISTENER_STREAMING_USER = "KAFKA_LISTENER_STREAMING_USER"
const val KAFKA_LISTENER_TASK_LIFECYCLE = "KAFKA_LISTENER_TASK_LIFECYCLE"

@Configuration
class KafkaConsumerConfig(
    private val configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
    private val kafkaContainerCustomizer: ObjectProvider<ContainerCustomizer<Any?, Any?, ConcurrentMessageListenerContainer<Any?, Any?>?>>,
    private val kafkaProperties: KafkaProperties,
    objectMapper: ObjectMapper
) {

    private val jsonSchemaValidator: SchemaValidator = JsonSchemaValidator(objectMapper)

    @Bean(KAFKA_LISTENER_TASK_LIFECYCLE)
    fun taskLifecycleKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, TaskLifecycleDto> {
        val consumerFactory = taskLifecycleConsumerFactory()
        return kafkaListenerContainerFactory(consumerFactory)
    }

    @Bean(KAFKA_LISTENER_STREAMING_TASK)
    fun taskStreamingKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, TaskDto> {
        val consumerFactory = taskStreamingConsumerFactory<String, TaskDto>()
        return kafkaListenerContainerFactory(consumerFactory)
    }

    @Bean(KAFKA_LISTENER_STREAMING_USER)
    fun userStreamingKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, UserDto> {
        val consumerFactory = taskStreamingConsumerFactory<String, UserDto>()
        return kafkaListenerContainerFactory(consumerFactory)
    }

    private fun <K, V> kafkaListenerContainerFactory(consumerFactory: ConsumerFactory<K, V>): ConcurrentKafkaListenerContainerFactory<K, V> {
        val factory = ConcurrentKafkaListenerContainerFactory<K, V>()
        @Suppress("UNCHECKED_CAST")
        configurer
            .configure(
                factory as ConcurrentKafkaListenerContainerFactory<Any?, Any?>,
                consumerFactory as ConsumerFactory<Any?, Any?>
            )
        kafkaContainerCustomizer
            .ifAvailable { factory.setContainerCustomizer(it) }
        return factory
    }

    private fun taskLifecycleConsumerFactory(): ConsumerFactory<String, TaskLifecycleDto> {
        val consumerFactory = consumerFactory<String, TaskLifecycleDto> {
            val jsonDeserializer = JsonDeserializer<TaskLifecycleDto>()
            jsonDeserializer.setTypeResolver { topic, data, headers -> detectLifecycleType(topic, data, headers) }
            setValueDeserializerSupplier { SchemaValidationDeserializer(jsonDeserializer, jsonSchemaValidator) }
        }
        return consumerFactory
    }

    private inline fun <K, reified V> taskStreamingConsumerFactory(): ConsumerFactory<K, V> {
        val consumerFactory = consumerFactory<K, V> {
            setValueDeserializerSupplier { SchemaValidationDeserializer<V>(jsonDeserializer(), jsonSchemaValidator) }
        }
        return consumerFactory
    }

    private fun <K, V> consumerFactory(cb: DefaultKafkaConsumerFactory<K, V>.() -> Unit = {}): ConsumerFactory<K, V> {
        val consumerFactory = DefaultKafkaConsumerFactory<K, V>(kafkaProperties.buildConsumerProperties())
        consumerFactory.cb()
        return consumerFactory
    }

    private inline fun <reified T> jsonDeserializer(): JsonDeserializer<T> {
        val jsonDeserializer = JsonDeserializer(T::class.java)
        jsonDeserializer.configure(mapOf(JsonDeserializer.USE_TYPE_INFO_HEADERS to false), false)
        return jsonDeserializer
    }

    private fun detectLifecycleType(topic: String, data: ByteArray?, headers: Headers): JavaType? {
        val typeFactory = TypeFactory.defaultInstance()
        return when (TaskLifecycleEvent.parse(headers.getLastAsString(PopugKafkaHeaders.EVENT_NAME))) {
            CREATED -> typeFactory.constructType(TaskCreated::class.java)
            REASSIGNED -> typeFactory.constructType(TaskReassigned::class.java)
            CLOSED -> typeFactory.constructType(TaskClosed::class.java)
            else -> typeFactory.constructMapType(Map::class.java, String::class.java, Object::class.java)
        }
    }
}

@Configuration
class KafkaProducerConfig(
    objectMapper: ObjectMapper
) {

    private val jsonSchemaValidator: SchemaValidator = JsonSchemaValidator(objectMapper)

    @Bean
    fun defaultKafkaProducerFactoryCustomizer(): DefaultKafkaProducerFactoryCustomizer {
        return DefaultKafkaProducerFactoryCustomizer {
            @Suppress("UNCHECKED_CAST")
            val producerFactory = it as DefaultKafkaProducerFactory<Any?, Any?>
            producerFactory.setValueSerializerSupplier { SchemaValidationSerializer(JsonSerializer<Any?>(), jsonSchemaValidator) }
        }
    }
}