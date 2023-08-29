package io.averkhoglyad.popug.tasks.config

import com.fasterxml.jackson.databind.ObjectMapper
import io.averkhoglyad.popug.schema.JsonSchemaValidator
import io.averkhoglyad.popug.schema.SchemaValidator
import io.averkhoglyad.popug.schema.kafka.SchemaValidationDeserializer
import io.averkhoglyad.popug.schema.kafka.SchemaValidationSerializer
import io.averkhoglyad.popug.tasks.core.event.UserDto
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

const val KAFKA_LISTENER_STREAMING_USER = "KAFKA_LISTENER_STREAMING_USER"

@Configuration
class KafkaConsumerConfig(
    private val configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
    private val kafkaContainerCustomizer: ObjectProvider<ContainerCustomizer<Any?, Any?, ConcurrentMessageListenerContainer<Any?, Any?>?>>,
    private val kafkaProperties: KafkaProperties,
    objectMapper: ObjectMapper
) {

    private val jsonSchemaValidator: SchemaValidator = JsonSchemaValidator(objectMapper)

    @Bean(KAFKA_LISTENER_STREAMING_USER)
    fun userStreamingKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, UserDto> {
        val consumerFactory = consumerFactory<String, UserDto>()
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

    private inline fun <K, reified V> consumerFactory(): ConsumerFactory<K, V> {
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
        return JsonDeserializer(T::class.java)
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