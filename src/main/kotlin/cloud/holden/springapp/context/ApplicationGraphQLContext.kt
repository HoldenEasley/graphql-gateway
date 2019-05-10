package cloud.holden.springapp.context

import com.expedia.graphql.annotations.GraphQLContext
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse

@GraphQLContext
class ApplicationGraphQLContext(
  val request: ServerHttpRequest,
  val response: ServerHttpResponse
)