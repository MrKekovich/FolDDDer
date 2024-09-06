package io.github.mrkekovich.folddders.template

fun getDtoTemplate(className: String, packageName: String): String {
    return """
        package $packageName.application.dto
        
        class $className
    """.trimIndent()
}

fun getRouteTemplate(routeName: String, packageName: String): String {
    val formattedRouteName = routeName.replaceFirstChar { it.lowercase() }

    return """
        package $packageName.application.route
        
        fun /*Routing.*/$formattedRouteName() {
            // TODO: add routes
        }
    """.trimIndent()
}

fun getUseCaseImplTemplate(useCaseName: String, packageName: String): String {
    return """
        package $packageName.application.usecase

        import $packageName.domain.usecase.$useCaseName
        
        class ${useCaseName}Impl : $useCaseName
    """.trimIndent()
}

fun getEntityTemplate(entityName: String, packageName: String): String {
    return """
        package $packageName.domain.entity
        
        class $entityName
    """.trimIndent()
}

fun getUseCaseTemplate(useCaseName: String, packageName: String): String {
    return """
        package $packageName.domain.usecase
        
        interface $useCaseName
    """.trimIndent()
}

fun getRepositoryTemplate(repositoryName: String, packageName: String): String {
    return """
        package $packageName.domain.repository
        
        interface $repositoryName
    """.trimIndent()
}

fun getRepositoryImplTemplate(repositoryName: String, packageName: String): String {
    return """
        package $packageName.infrastructure.repository
        
        import $packageName.domain.repository.$repositoryName
        
        class ${repositoryName}Impl : $repositoryName
    """.trimIndent()
}

fun getTableTemplate(tableName: String, packageName: String): String {
    return """
        package $packageName.infrastructure.persistence
        
        object $tableName
    """.trimIndent()
}

fun getDaoTemplate(daoName: String, packageName: String): String {
    return """
        package $packageName.infrastructure.persistence
        
        class $daoName
    """.trimIndent()
}
