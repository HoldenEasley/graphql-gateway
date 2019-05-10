package cloud.holden.springapp.validation

import com.expedia.graphql.execution.DataFetcherExecutionPredicate
import cloud.holden.springapp.exceptions.ValidationException
import cloud.holden.springapp.exceptions.asConstraintError
import graphql.schema.DataFetchingEnvironment
import javax.validation.Valid
import javax.validation.Validator
import kotlin.reflect.KParameter

class DataFetcherExecutionValidator(private val validator: Validator) : DataFetcherExecutionPredicate {

  override fun <T> evaluate(value: T, parameter: KParameter, environment: DataFetchingEnvironment): T {
    val parameterAnnotated = parameter.annotations.any { it.annotationClass == Valid::class }
    val validations = if (parameterAnnotated) {
      validator.validate(value)
    } else {
      emptySet()
    }

    if (validations.isEmpty()) {
      return value
    } else {
      throw ValidationException(validations.map { it.asConstraintError() })
    }
  }
}