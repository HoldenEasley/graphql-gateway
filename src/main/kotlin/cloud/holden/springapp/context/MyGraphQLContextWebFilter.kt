package cloud.holden.springapp.context

import cloud.holden.springapp.context.ApplicationGraphQLContext
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class MyGraphQLContextWebFilter : WebFilter {

  override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
    val customContext = ApplicationGraphQLContext(
      request = exchange.request,
      response = exchange.response
    )
    return chain.filter(exchange).subscriberContext { it.put("graphQLContext", customContext) }
  }
}