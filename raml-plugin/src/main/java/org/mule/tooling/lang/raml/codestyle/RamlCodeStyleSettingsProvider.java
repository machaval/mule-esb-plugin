package org.mule.tooling.lang.raml.codestyle;

import org.jetbrains.yaml.YAMLLanguageCodeStyleSettingsProvider;
import org.mule.tooling.lang.raml.RamlLanguage;

/**
 * Created by eberman on 4/18/17.
 */
public class RamlCodeStyleSettingsProvider extends YAMLLanguageCodeStyleSettingsProvider {

    @Override
    public String getConfigurableDisplayName() {
        return RamlLanguage.INSTANCE.getDisplayName();
    }

}
