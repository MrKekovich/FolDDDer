package io.github.mrkekovich.folddders.action

import com.intellij.codeInsight.daemon.JavaErrorBundle
import com.intellij.codeInsight.daemon.impl.analysis.HighlightClassUtil
import com.intellij.icons.AllIcons
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.actions.CreateTemplateInPackageAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.JavaDirectoryService
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiNameHelper
import com.intellij.psi.util.PsiUtil
import io.github.mrkekovich.folddders.asset.PlatformAssets
import io.github.mrkekovich.folddders.template.getDaoTemplate
import io.github.mrkekovich.folddders.template.getDtoTemplate
import io.github.mrkekovich.folddders.template.getEntityTemplate
import io.github.mrkekovich.folddders.template.getRepositoryImplTemplate
import io.github.mrkekovich.folddders.template.getRepositoryTemplate
import io.github.mrkekovich.folddders.template.getRouteTemplate
import io.github.mrkekovich.folddders.template.getTableTemplate
import io.github.mrkekovich.folddders.template.getUseCaseImplTemplate
import io.github.mrkekovich.folddders.template.getUseCaseTemplate
import org.jetbrains.jps.model.java.JavaModuleSourceRootTypes
import kotlin.reflect.KFunction2

private const val WITHOUT_FILES = "DDD_EMPTY"
private const val WITH_FILES = "DDD_WITH_FILES"

class CreateDomainFolderStructure :
    CreateTemplateInPackageAction<PsiClass>(
        CAPTION,
        "Domain Folder Structure",
        PlatformAssets.DDD_LOGO,
        JavaModuleSourceRootTypes.SOURCES,
    ),
    DumbAware {

    override fun getActionName(directory: PsiDirectory?, newName: String, templateName: String?): String = CAPTION

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle(CAPTION)
        builder.setValidator(ClassInputValidator(project, directory))

        builder.addKind("Without files", AllIcons.Actions.NewFolder, WITHOUT_FILES)
        builder.addKind("With files", AllIcons.Actions.AddFile, WITH_FILES)
    }

    override fun checkPackageExists(directory: PsiDirectory): Boolean {
        val pkg = JavaDirectoryService.getInstance().getPackage(directory) ?: return false

        val name = pkg.qualifiedName
        return StringUtil.isEmpty(name) || PsiNameHelper.getInstance(directory.project).isQualifiedName(name)
    }

    override fun getNavigationElement(createdElement: PsiClass): PsiElement? {
        return createdElement.lBrace
    }

    override fun doCreate(dir: PsiDirectory, inputName: String, templateName: String): PsiClass? {
        val dddStructure = addDirectories(dir, inputName)

        if (templateName == WITH_FILES) dddStructure.createFiles()

        return null
    }

    private class ClassInputValidator(
        private val project: Project,
        private val directory: PsiDirectory,
    ) : InputValidatorEx {
        override fun getErrorText(inputString: String): String? {
            if (inputString.isNotEmpty() && !PsiNameHelper.getInstance(project).isQualifiedName(inputString)) {
                return JavaErrorBundle.message("create.class.action.this.not.valid.java.qualified.name")
            }

            val shortName = StringUtil.getShortName(inputString)
            val languageLevel = PsiUtil.getLanguageLevel(directory)
            return if (HighlightClassUtil.isRestrictedIdentifier(shortName, languageLevel)) {
                JavaErrorBundle.message("restricted.identifier", shortName)
            } else {
                null
            }
        }

        override fun checkInput(inputString: String): Boolean =
            inputString.isNotBlank() && getErrorText(inputString) == null

        override fun canClose(inputString: String): Boolean =
            checkInput(inputString)
    }

    private companion object {
        private const val CAPTION = "New Domain"
    }
}

private fun addDirectories(
    dir: PsiDirectory,
    inputName: String,
): DDDFolderStructure {
    val parent = dir.createSubdirectory(inputName)

    val application = parent.createSubdirectory("application")
    val applicationDto = application.createSubdirectory("dto")
    val applicationRoute = application.createSubdirectory("route")
    val applicationUseCase = application.createSubdirectory("usecase")

    val domain = parent.createSubdirectory("domain")
    val domainEntity = domain.createSubdirectory("entity")
    val domainRepository = domain.createSubdirectory("repository")
    val domainUseCase = domain.createSubdirectory("usecase")

    val infrastructure = parent.createSubdirectory("infrastructure")
    val infrastructurePersistence = infrastructure.createSubdirectory("persistence")
    val infrastructureRepository = infrastructure.createSubdirectory("repository")

    return DDDFolderStructure(
        name = inputName,
        parent = parent,
        applicationDto = applicationDto,
        applicationRoute = applicationRoute,
        applicationUseCase = applicationUseCase,
        domainEntity = domainEntity,
        domainRepository = domainRepository,
        domainUseCase = domainUseCase,
        infrastructurePersistence = infrastructurePersistence,
        infrastructureRepository = infrastructureRepository,
    )
}

private data class DDDFolderStructure(
    val name: String,
    val parent: PsiDirectory,
    val applicationDto: PsiDirectory,
    val applicationRoute: PsiDirectory,
    val applicationUseCase: PsiDirectory,
    val domainEntity: PsiDirectory,
    val domainRepository: PsiDirectory,
    val domainUseCase: PsiDirectory,
    val infrastructurePersistence: PsiDirectory,
    val infrastructureRepository: PsiDirectory
) {
    fun createFiles() {
        val name = name.replaceFirstChar { it.uppercase() }
        val packageName = JavaDirectoryService.getInstance().getPackage(parent)!!.qualifiedName

        applicationDto.createFile(parent, "${name}Request", ::getDtoTemplate)
        applicationDto.createFile(parent, "${name}Response", ::getDtoTemplate)

        applicationRoute.createFile(parent, "${name}Routes", ::getRouteTemplate)

        applicationUseCase.createFile(parent, "${name}UseCaseImpl", ::getUseCaseImplTemplate)

        domainEntity.createFile(parent, "${name}Entity", ::getEntityTemplate)
        domainRepository.createFile(parent, "${name}Repository", ::getRepositoryTemplate)
        domainUseCase.createFile(parent, "${name}UseCase", ::getUseCaseTemplate)

        infrastructurePersistence.createFile(parent, "${name}Table", ::getTableTemplate)
//        infrastructurePersistence.createFile(parent, "${name}Dao", ::getDaoTemplate) // TODO: Uncomment if needed.

        infrastructureRepository.createFile(parent, "${name}RepositoryImpl", ::getRepositoryImplTemplate)
    }
}

private fun PsiDirectory.createFile(
    parent: PsiDirectory,
    name: String,
    template: (String, String) -> String
) {
    val packageName = JavaDirectoryService.getInstance().getPackage(parent)?.qualifiedName

    check(packageName != null) {
        "Could not determine package name for directory: $this"
    }

    val file = createFile("$name.kt")
    val document = PsiDocumentManager.getInstance(project).getDocument(file) ?: return

    document.setText(template(name, packageName))
}
