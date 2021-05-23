package org.mule.tooling.esb.debugger.breakpoint;

import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlTag;
import com.intellij.xdebugger.XDebuggerUtil;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointProperties;
import com.intellij.xdebugger.breakpoints.XLineBreakpoint;
import com.intellij.xdebugger.breakpoints.XLineBreakpointType;
import com.intellij.xdebugger.evaluation.XDebuggerEditorsProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveLanguage;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.esb.util.MuleElementType;
import org.mule.tooling.esb.util.MuleConfigUtils;


public class MuleBreakpointType extends XLineBreakpointType<XBreakpointProperties>
{
    protected MuleBreakpointType()
    {
        super("mule", "Mule Breakpoints");
    }

    @Override
    public boolean canPutAt(@NotNull VirtualFile file, int line, @NotNull Project project)
    {
        final Document document = FileDocumentManager.getInstance().getDocument(file);
        if (document != null)
        {
            final PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (psiFile != null)
            {
                final boolean isMuleAndXml = MuleConfigUtils.isMuleFile(psiFile);
                if (isMuleAndXml)
                {
                    final XmlTag xmlTagAt = MuleConfigUtils.getXmlTagAt(project, XDebuggerUtil.getInstance().createPosition(file, line));
                    if (xmlTagAt != null)
                    {
                        return MuleConfigUtils.getMuleElementTypeFromXmlElement(xmlTagAt) == MuleElementType.MESSAGE_PROCESSOR;
                    }
                    else
                    {
                        final PsiElement firstWeaveElement = WeavePsiUtils.getFirstWeaveElement(project, document, line);
                        if (firstWeaveElement != null)
                        {
                            final PsiElement elementInInjected = InjectedLanguageManager.getInstance(project)
                                    .findInjectedElementAt(psiFile, line);
                            if (elementInInjected != null) {
                                final Language language = elementInInjected.getLanguage();
                                return language == WeaveLanguage.getInstance();
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public XDebuggerEditorsProvider getEditorsProvider(@NotNull XLineBreakpoint<XBreakpointProperties> breakpoint, @NotNull Project project)
    {
        final XSourcePosition position = breakpoint.getSourcePosition();
        if (position == null)
        {
            return null;
        }

        final PsiFile file = PsiManager.getInstance(project).findFile(position.getFile());
        if (file == null)
        {
            return null;
        }

        return new MuleDebuggerEditorsProvider();
    }

    @Nullable
    @Override
    public XBreakpointProperties createBreakpointProperties(@NotNull VirtualFile virtualFile, int i)
    {
        return null;
    }


}
