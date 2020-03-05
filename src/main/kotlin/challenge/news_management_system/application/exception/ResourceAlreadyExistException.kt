package challenge.news_management_system.application.exception

class ResourceAlreadyExistException(resourceUniqueKey: String, resourceClass: Class<*>)
    : RuntimeException("${resourceClass.simpleName}[$resourceUniqueKey] already exist")