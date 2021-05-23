package org.mule.tooling.esb.launcher.configuration;

import com.intellij.execution.BeforeRunTask;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;


class MuleConfigurationFactory extends ConfigurationFactory
{

    public MuleConfigurationFactory(MuleConfigurationType configurationType)
    {
        super(configurationType);
    }

    public RunConfiguration createTemplateConfiguration(Project project)
    {
        return new MuleConfiguration("Mule ESB", this, project);
    }

    @Override
    public @NotNull
    @NonNls String getId() {
        return "Mule ESB";
    }

    @Override
    public void configureBeforeRunTaskDefaults(Key<? extends BeforeRunTask> providerID, BeforeRunTask task)
    {
        super.configureBeforeRunTaskDefaults(providerID, task);
    }
}
