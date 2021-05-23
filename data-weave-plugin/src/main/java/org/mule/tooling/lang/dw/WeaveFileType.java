package org.mule.tooling.lang.dw;


import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class WeaveFileType extends LanguageFileType {

    public static WeaveFileType INSTANCE = new WeaveFileType();

    protected WeaveFileType() {
        super(WeaveLanguage.getInstance());
    }

    @NotNull
    @Override
    public String getName() {
        return "DataWeave";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "DataWeave transformation file.";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "dwl";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return WeaveIcons.DataFileType;
    }

    public List<String> getExtensions() {
        List<String> extensions = new ArrayList<>();
        extensions.add("dwl");
        extensions.add("dw");
        extensions.add("wev");

        return extensions;
    }

    public static WeaveFileType getInstance() {
        return INSTANCE;
    }
}
