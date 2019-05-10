package cloud.holden.springapp.directive

import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.WiringFactory
import org.springframework.stereotype.Component

@Component
class DirectiveWiringFactory : WiringFactory {
  private val wiring = listOf(
    StringEvalDirectiveWiring()
  )

  override fun providesSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>): Boolean = true

  override fun getSchemaDirectiveWiring(environment: SchemaDirectiveWiringEnvironment<*>): SchemaDirectiveWiring? =
    wiring.firstOrNull {
      it.isApplicable(environment)
    }
}