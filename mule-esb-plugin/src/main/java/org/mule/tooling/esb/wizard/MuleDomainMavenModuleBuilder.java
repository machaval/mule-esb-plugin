package org.mule.tooling.esb.wizard;

import com.intellij.facet.*;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import org.mule.tooling.esb.framework.facet.MuleFacet;
import org.mule.tooling.esb.util.MuleIcons;

import javax.swing.*;
import java.io.File;

public class MuleDomainMavenModuleBuilder extends MavenModuleBuilder implements SourcePathsBuilder, MuleModuleBuilder {

    public static final String DEFAULT_MULE_VERSION = "3.8.2";
    private String muleVersion = DEFAULT_MULE_VERSION;

    public MuleDomainMavenModuleBuilder() {
        setProjectId(new MavenId("org.mule.app", "my-domain", "1.0.0-SNAPSHOT"));
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);

        setMuleFacet(rootModel.getModule());

        final Project project = rootModel.getProject();
        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);
        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> new MuleDomainMavenProjectBuilderHelper().configure(project, getProjectId(), muleVersion, root));
    }

    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(this.getContentEntryPath());
        (new File(path)).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public String getName() {
        return "Mule Domain";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }


    @Override
    public String getPresentableName() {
        return "Mule Domain";
    }

    @Override
    public Icon getNodeIcon() {
        return MuleIcons.MuleIcon;
    }

    @Override
    public String getDescription() {
        return "Creates a Mule ESB Domain Maven Based Project";
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        MuleVersionConfiguration step = new MuleVersionConfiguration(this, muleVersion);
        Disposer.register(parentDisposable, step);
        return step;
    }

    public void setMuleVersion(String muleVersion) {
        this.muleVersion = muleVersion;
    }

    public void setMuleFacet(Module module) {
        FacetType type = FacetTypeRegistry.getInstance().findFacetType(MuleFacet.ID);
        Facet facet = type.createFacet(module, type.getPresentableName(), type.createDefaultConfiguration(), null);
        ModifiableFacetModel model = FacetManager.getInstance(module).createModifiableModel();
        model.addFacet(facet);
        model.commit();
    }

}
