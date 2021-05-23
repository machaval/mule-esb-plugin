package org.mule.tooling.esb.templates;


import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import icons.MavenIcons;
import org.mule.tooling.esb.util.MuleIcons;

public class MuleFileTemplateDescriptorManager implements FileTemplateGroupDescriptorFactory {
    public static final String MULE_CONFIGURATION_FILE = "Mule Config";
    public static final String MULE_DOMAIN_CONFIGURATION_FILE = "Mule Domain Config";
    public static final String DATA_WEAVE_FILE = "Data Weave";
    public static final String MEL_FILE = "Mel File";
    public static final String MULE_MAVEN_PROJECT = "Mule Maven Project";
    public static final String MULE_MAVEN_MODULE = "Mule Maven Module";
    public static final String MULE_DOMAIN_MAVEN_PROJECT = "Mule Domain Maven Project";
    public static final String MULE_DEPLOY_PROPERTIES = "Mule Deploy Properties";
    public static final String MULE_DOMAIN_DEPLOY_PROPERTIES = "Mule Domain Deploy Properties";
    public static final String LOG4J2 = "log4j2";
    public static final String LOG4J2_TEST = "log4j2-test";
    public static final String MUNIT_FILE = "MUnit Config";
    public static final String RAML_FILE = "RAML File";

    @Override
    public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
        final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("Mule", MuleIcons.MuleIcon);
        group.addTemplate(new FileTemplateDescriptor(MULE_CONFIGURATION_FILE, MuleIcons.MuleFileType));
        group.addTemplate(new FileTemplateDescriptor(MULE_DOMAIN_CONFIGURATION_FILE, MuleIcons.MuleFileType));
        group.addTemplate(new FileTemplateDescriptor(DATA_WEAVE_FILE, MuleIcons.DataFileType));
        group.addTemplate(new FileTemplateDescriptor(MULE_MAVEN_PROJECT, MavenIcons.MavenPlugin));
        group.addTemplate(new FileTemplateDescriptor(MULE_MAVEN_MODULE, MavenIcons.MavenPlugin));
        group.addTemplate(new FileTemplateDescriptor(MULE_DOMAIN_MAVEN_PROJECT, MavenIcons.MavenPlugin));
        group.addTemplate(new FileTemplateDescriptor(MUNIT_FILE, MuleIcons.MUnitFileType));
        group.addTemplate(new FileTemplateDescriptor(RAML_FILE, MuleIcons.RamlFileType));

        return group;
    }
}
