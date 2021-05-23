package org.mule.tooling.esb.framework.facet;

import com.intellij.facet.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.*;
import org.mule.tooling.esb.framework.MuleFrameworkUtil;
import org.mule.tooling.esb.util.MuleIcons;

import javax.swing.*;

public class MuleFacetType extends FacetType<MuleFacet, MuleFacetConfiguration> {
  public static final FacetTypeId<MuleFacet> TYPE_ID = new FacetTypeId<MuleFacet>(MuleFacet.ID);

  public MuleFacetType() {
    super(TYPE_ID, MuleFacet.ID, "Mule ESB");
  }

  @Override
  public MuleFacetConfiguration createDefaultConfiguration() {
    return new MuleFacetConfiguration();
  }

  @Override
  public MuleFacet createFacet(@NotNull Module module,
                               String s,
                               @NotNull MuleFacetConfiguration configuration,
                               Facet facet) {
    return new MuleFacet(this, module, s, configuration, facet);
  }

  @Override
  public boolean isSuitableModuleType(ModuleType type) {
    return MuleFrameworkUtil.isAcceptableModuleType(type);
  }

  @Nullable
  @Override
  public Icon getIcon() {
    return MuleIcons.MuleIcon;
  }
}
