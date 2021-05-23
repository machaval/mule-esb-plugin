package org.mule.tooling.esb.lang.mel;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.esb.util.MuleIcons;

import javax.swing.*;


public class MelFileType extends LanguageFileType {

    public static MelFileType INSTANCE = new MelFileType();

    protected MelFileType() {
        super(MelLanguage.getInstance());
    }

    @NotNull
    @Override
    public String getName() {
        return "Mel";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Mel";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "mel";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return MuleIcons.MelFileType;
    }

    public static MelFileType getInstance(){
        return INSTANCE;
    }
}
