package org.mule.tooling.esb.wizard;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.mule.tooling.esb.templates.MuleFileTemplateDescriptorManager;

import java.io.IOException;
import java.util.Properties;

import static com.intellij.openapi.command.WriteCommandAction.writeCommandAction;

public class MuleDomainMavenProjectBuilderHelper
{

    public void configure(final Project project, final MavenId projectId, final String muleVersion, final VirtualFile root)
    {
        try
        {
            //Create mule folders.
            final VirtualFile appDirectory = VfsUtil.createDirectories(root.getPath() + "/src/main/domain");
            final VirtualFile resources = VfsUtil.createDirectories(root.getPath() + "/src/main/resources");
            final VirtualFile muleConfigFile = createMuleConfigFile(project, projectId, appDirectory);
            createMuleDeployPropertiesFile(project, projectId, appDirectory);
            createPomFile(project, projectId, muleVersion, root);
            // execute when current dialog is closed (e.g. Project Structure)
            MavenUtil.invokeLater(project, ModalityState.NON_MODAL, () -> EditorHelper.openInEditor(getPsiFile(project, muleConfigFile)));

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static PsiFile getPsiFile(Project project, VirtualFile pom)
    {
        return PsiManager.getInstance(project).findFile(pom);
    }

    private VirtualFile createMuleConfigFile(final Project project, final MavenId projectId, final VirtualFile appDirectory) throws IOException {
        final String domainConfigName = "mule-domain-config"; //Currently Mule requires it to be mule-domain-config.xml

        return writeCommandAction(project, PsiFile.EMPTY_ARRAY)
                .compute(() -> {
                    VirtualFile configFile = appDirectory.findOrCreateChildData(this, domainConfigName + ".xml");
                    final Properties templateProps = new Properties();
                    templateProps.setProperty("NAME", projectId.getArtifactId());
                    final FileTemplateManager manager = FileTemplateManager.getInstance(project);
                    final FileTemplate template = manager.getInternalTemplate(MuleFileTemplateDescriptorManager.MULE_DOMAIN_CONFIGURATION_FILE);
                    final Properties defaultProperties = manager.getDefaultProperties();
                    defaultProperties.putAll(templateProps);
                    final String text = template.getText(defaultProperties);
                    VfsUtil.saveText(configFile, text);
                    return configFile;
                });
    }

    private VirtualFile createMuleDeployPropertiesFile(final Project project, final MavenId projectId, final VirtualFile appDirectory) throws IOException {
        return writeCommandAction(project, PsiFile.EMPTY_ARRAY)
                .compute(() -> {
                    VirtualFile configFile = appDirectory.findOrCreateChildData(this, "mule-deploy.properties");
                    final Properties templateProps = new Properties();
                    templateProps.setProperty("NAME", projectId.getArtifactId());
                    final FileTemplateManager manager = FileTemplateManager.getInstance(project);
                    final FileTemplate template = manager.getInternalTemplate(MuleFileTemplateDescriptorManager.MULE_DOMAIN_DEPLOY_PROPERTIES);
                    final Properties defaultProperties = manager.getDefaultProperties();
                    defaultProperties.putAll(templateProps);
                    final String text = template.getText(defaultProperties);
                    VfsUtil.saveText(configFile, text);
                    return configFile;
                });
    }

    private static void showError(Project project, Throwable e)
    {
        MavenUtil.showError(project, "Failed to create a Mule Domain project", e);
    }

    private VirtualFile createPomFile(final Project project, final MavenId projectId, final String muleVersion, final VirtualFile root) throws IOException {
        return writeCommandAction(project, PsiFile.EMPTY_ARRAY)
                .compute(() -> {
                    VirtualFile pomFile = root.findOrCreateChildData(this, MavenConstants.POM_XML);
                    final Properties templateProps = new Properties();
                    templateProps.setProperty("GROUP_ID", projectId.getGroupId());
                    templateProps.setProperty("ARTIFACT_ID", projectId.getArtifactId());
                    templateProps.setProperty("VERSION", projectId.getVersion());
                    templateProps.setProperty("MULE_VERSION", muleVersion);
                    final FileTemplateManager manager = FileTemplateManager.getInstance(project);
                    final FileTemplate template = manager.getInternalTemplate(MuleFileTemplateDescriptorManager.MULE_DOMAIN_MAVEN_PROJECT);
                    final Properties defaultProperties = manager.getDefaultProperties();
                    defaultProperties.putAll(templateProps);
                    final String text = template.getText(defaultProperties);
                    VfsUtil.saveText(pomFile, text);
                    return pomFile;
                });
    }
}
