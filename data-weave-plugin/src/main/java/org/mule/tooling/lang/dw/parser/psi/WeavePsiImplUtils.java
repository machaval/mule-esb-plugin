package org.mule.tooling.lang.dw.parser.psi;


import com.intellij.icons.AllIcons;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.*;
import java.util.List;

public class WeavePsiImplUtils {

    public static ItemPresentation getPresentation(WeaveDocument document) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return "Document";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return WeaveIcons.DataFileType;
            }
        };
    }

    public static ItemPresentation getPresentation(WeaveObjectExpression document) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return "";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Json.Object;
            }
        };
    }

    public static ItemPresentation getPresentation(WeaveArrayExpression document) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return "";
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Json.Array;
            }
        };
    }

    public static ItemPresentation getPresentation(final WeaveSimpleKeyValuePair kvp) {
        return new ItemPresentation() {

            @Nullable
            @Override
            public String getPresentableText() {
                return kvp.getKey().getExpression().getText();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return AllIcons.Nodes.Property;
            }
        };
    }

    @Nullable
    public static WeaveExpression getLeft(WeaveBinaryExpression expression) {
        final List<WeaveExpression> expressionList = expression.getExpressionList();
        if (!expressionList.isEmpty()) {
            return expressionList.get(0);
        } else {
            return null;
        }
    }

    @Nullable
    public static WeaveExpression getRight(WeaveBinaryExpression expression) {
        final List<WeaveExpression> expressionList = expression.getExpressionList();
        if (expressionList.size() > 1) {
            return expressionList.get(1);
        } else {
            return null;
        }
    }

    @Nullable
    public static WeaveExpression getLeft(WeaveBinaryClojureExpression expression) {
        final List<WeaveExpression> expressionList = expression.getExpressionList();
        if (!expressionList.isEmpty()) {
            return expressionList.get(0);
        } else {
            return null;
        }
    }

    @Nullable
    public static WeaveExpression getRight(WeaveBinaryClojureExpression expression) {
        final List<WeaveExpression> expressionList = expression.getExpressionList();
        if (expressionList.size() > 1) {
            return expressionList.get(1);
        } else {
            return null;
        }
    }

    @NotNull
    public static String getValue(WeaveStringLiteral stringLiteral) {
        return WeavePsiUtils.stripQuotes(stringLiteral.getText());
    }

    public static String getName(WeaveIdentifier identifier) {
        return identifier.getText();
    }

    //Variable Reference
    public static String getVariableName(WeaveVariableReferenceExpression variable) {
        return variable.getText();
    }

    public static PsiReference getReference(WeaveVariableReferenceExpression variable) {
        return new WeaveVariablePsiReference(variable, new TextRange(0, variable.getName().length()));
    }

}
