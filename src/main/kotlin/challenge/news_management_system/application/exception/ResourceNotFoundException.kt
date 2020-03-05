package challenge.news_management_system.application.exception

class ResourceNotFoundException(resourceBusinessKey: String, resourceClass: Class<*>)
    : RuntimeException("${resourceClass.simpleName}[$resourceBusinessKey] not found")
