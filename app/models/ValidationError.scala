package models

final case class ValidationError(field: String, message: String)
