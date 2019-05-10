package cloud.holden.springapp

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import cloud.holden.springapp.context.ApplicationGraphQLContext
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class QueryHandler(private val graphql: GraphQL) {

  fun executeQuery(request: GraphQLRequest): Mono<GraphQLResponse> = Mono.subscriberContext()
    .flatMap { ctx ->
      val graphQLContext: ApplicationGraphQLContext = ctx.get("graphQLContext")
      val input = ExecutionInput.newExecutionInput()
        .query(request.query)
        .operationName(request.operationName)
        .variables(request.variables)
        .context(graphQLContext)
        .build()

      Mono.fromFuture(graphql.executeAsync(input))
        .map { executionResult -> executionResult.toGraphQLResponse() }
    }

}

data class GraphQLRequest(
  val query: String,
  val operationName: String? = null,
  val variables: Map<String, Any>? = null
)

@JsonInclude(Include.NON_NULL)
data class GraphQLResponse(
  val data: Any? = null,
  val errors: List<Any>? = null,
  val extensions: Map<Any, Any>? = null
)

/**
 * Convert ExecutionResult to GraphQLResponse.
 *
 * NOTE: we need this as graphql-java defaults to only serializing GraphQLError objects so any custom error fields are ignored.
 */
internal fun ExecutionResult.toGraphQLResponse(): GraphQLResponse {
  val filteredErrors = if (errors?.isNotEmpty() == true) errors else null
  val filteredExtensions = if (extensions?.isNotEmpty() == true) extensions else null
  return GraphQLResponse(getData(), filteredErrors, filteredExtensions)
}