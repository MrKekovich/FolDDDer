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
    val interfaceName = useCaseName.replace("Impl", "")

    return """
        package $packageName.application.usecase

        import $packageName.domain.usecase.$interfaceName
        
        class $useCaseName : $interfaceName
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
    val interfaceName = repositoryName.replace("Impl", "")

    return """
        package $packageName.infrastructure.repository
        
        import $packageName.domain.repository.$interfaceName
        
        class $repositoryName : $interfaceName
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
