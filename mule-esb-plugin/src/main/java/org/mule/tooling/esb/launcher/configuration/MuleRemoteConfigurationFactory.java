package org.mule.tooling.esb.launcher.configuration;

import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;


class MuleRemoteConfigurationFactory extends ConfigurationFactory {

  public MuleRemoteConfigurationFactory(MuleRemoteConfigurationType configurationType) {
    super(configurationType);
  }

  public RunConfiguration createTemplateConfiguration(Project project) {
    return new MuleRemoteConfiguration(project, this, "Remote debug");
  }

  @Override
  public
  String getId() {
    return "Mule ESB Plugin For IntelliJ";
  }

}
