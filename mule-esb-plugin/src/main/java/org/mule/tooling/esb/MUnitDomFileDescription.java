package org.mule.tooling.esb;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.esb.config.model.MUnit;
import org.mule.tooling.esb.util.MuleConfigUtils;
import org.mule.tooling.esb.util.MuleIcons;

import javax.swing.*;

public class MUnitDomFileDescription extends DomFileDescription<MUnit> {
    public MUnitDomFileDescription() {
        super(MUnit.class, "mule", "http://www.mulesoft.org/schema/mule/core");
    }

    @Override
    public boolean isMyFile(@NotNull XmlFile file, @Nullable Module module) {
        return MuleConfigUtils.isMUnitFile(file);
    }

    @Nullable
    public Icon getFileIcon(@Iconable.IconFlags int flags) {
        return MuleIcons.MUnitFileType;
    }

    @Override
    protected void initializeFileDescription() {
        super.initializeFileDescription();
    }

    @Override
    public boolean acceptsOtherRootTagNames() {
        return true;
    }
}
