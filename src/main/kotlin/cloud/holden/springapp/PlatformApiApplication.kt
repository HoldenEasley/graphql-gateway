package cloud.holden.springapp

import com.expedia.graphql.DirectiveWiringHelper
import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import com.expedia.graphql.toSchema
import cloud.holden.springapp.datafetcher.CustomDataFetcherFactoryProvider
import cloud.holden.springapp.datafetcher.SpringDataFetcherFactory
import cloud.holden.springapp.directive.DirectiveWiringFactory
import cloud.holden.springapp.directive.LowercaseDirectiveWiring
import cloud.holden.springapp.extensions.CustomSchemaGeneratorHooks
import cloud.holden.springapp.query.Query
import graphql.GraphQL
import graphql.execution.AsyncExecutionStrategy
import graphql.execution.AsyncSerialExecutionStrategy
import graphql.execution.DataFetcherExceptionHandler
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import javax.validation.Validator

@SpringBootApplication
class PlatformApiApplication {
  private val logger = LoggerFactory.getLogger(PlatformApiApplication::class.java)

  @Bean
  fun hooks(validator: Validator, wiringFactory: DirectiveWiringFactory) =
    CustomSchemaGeneratorHooks(
      validator,
      DirectiveWiringHelper(wiringFactory, mapOf("lowercase" to LowercaseDirectiveWiring()))
    )

  @Bean
  fun dataFetcherFactoryProvider(springDataFetcherFactory: SpringDataFetcherFactory, hooks: SchemaGeneratorHooks) =
    CustomDataFetcherFactoryProvider(springDataFetcherFactory, hooks)

  @Bean
  fun schemaConfig(
    hooks: SchemaGeneratorHooks,
    dataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider
  ): SchemaGeneratorConfig = SchemaGeneratorConfig(
    supportedPackages = listOf("com.recruitics.platformapi"),
    hooks = hooks,
    dataFetcherFactoryProvider = dataFetcherFactoryProvider
  )


  @Bean
  fun schemaPrinter() = SchemaPrinter(
    SchemaPrinter.Options.defaultOptions()
      .includeScalarTypes(true)
      .includeExtendedScalarTypes(true)
      .includeIntrospectionTypes(true)
      .includeSchemaDefintion(true)
  )

  @Bean
  fun graphQL(
    schema: GraphQLSchema,
    dataFetcherExceptionHandler: DataFetcherExceptionHandler
  ): GraphQL = GraphQL.newGraphQL(schema)
    .queryExecutionStrategy(AsyncExecutionStrategy(dataFetcherExceptionHandler))
    .mutationExecutionStrategy(AsyncSerialExecutionStrategy(dataFetcherExceptionHandler))
    .build()

  @Bean
  fun schema(
    queries: List<Query>,
//    mutations: List<Mutation>,
    schemaConfig: SchemaGeneratorConfig,
    schemaPrinter: SchemaPrinter
  ): GraphQLSchema {
    fun List<Any>.toTopLevelObjectDefs() = this.map {
      TopLevelObject(it)
    }

      val schema = toSchema(
      config = schemaConfig,
      queries = queries.toTopLevelObjectDefs()
//      mutations = mutations.toTopLevelObjectDefs()
    )

    logger.info(schemaPrinter.print(schema))
    return schema
  }
}

fun main(args: Array<String>) {
  runApplication<PlatformApiApplication>(*args)
}
