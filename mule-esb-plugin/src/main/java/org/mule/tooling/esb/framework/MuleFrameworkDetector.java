/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mule.tooling.esb.framework;

import com.intellij.framework.FrameworkType;
import com.intellij.framework.addSupport.impl.AddSupportForSingleFrameworkDialog;
import com.intellij.framework.detection.DetectedFrameworkDescription;
import com.intellij.framework.detection.FileContentPattern;
import com.intellij.framework.detection.FrameworkDetectionContext;
import com.intellij.framework.detection.FrameworkDetector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.patterns.ElementPattern;
import com.intellij.util.indexing.FileContent;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.esb.sdk.MuleSdk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


public class MuleFrameworkDetector extends FrameworkDetector
{
    public MuleFrameworkDetector()
    {
        super("mule-esb");
    }


    @Override
    public @NotNull FrameworkType getFrameworkType()
    {
        return MuleFrameworkType.getFrameworkType();
    }


    @NotNull
    @Override
    public FileType getFileType()
    {
        return StdFileTypes.PROPERTIES;
    }

    @NotNull
    @Override
    public ElementPattern<FileContent> createSuitableFilePattern()
    {
        return FileContentPattern.fileContent().andOr(
                        FileContentPattern.fileContent().withName("mule-app.properties"),
                        FileContentPattern.fileContent().withName("mule-domain-config.xml"));

    }

    @Override
    public List<? extends DetectedFrameworkDescription> detect(@NotNull Collection<VirtualFile> collection, @NotNull FrameworkDetectionContext context)
    {
        List<MuleFrameworkDetected> detectedFrameworkDescriptions = new ArrayList<>();

        if (!collection.isEmpty() && context.getProject() != null)
        {
            for (VirtualFile nextDetectedFile : collection) {
                final Module moduleForFile = ProjectRootManager.getInstance(context.getProject()).getFileIndex().getModuleForFile(nextDetectedFile);
                if (MuleSdk.getFrom(moduleForFile) == null) {
                    final List<VirtualFile> relatedFiles = new ArrayList<>();
                    relatedFiles.add(nextDetectedFile);
                    detectedFrameworkDescriptions.add(new MuleFrameworkDetected(this, relatedFiles, context));
                }
            }

        }
        return detectedFrameworkDescriptions;
    }

    private class MuleFrameworkDetected extends DetectedFrameworkDescription
    {

        private MuleFrameworkDetector detector;
        private Collection<VirtualFile> collection;
        private FrameworkDetectionContext context;

        public MuleFrameworkDetected(MuleFrameworkDetector detector, Collection<VirtualFile> collection, FrameworkDetectionContext context)
        {
            this.detector = detector;
            this.collection = collection;
            this.context = context;
        }

        @NotNull @Override public Collection<? extends VirtualFile> getRelatedFiles()
        {
            return collection;
        }

        @NotNull
        @Override
        public String getSetupText()
        {
            return "Mule Project was detected.";
        }

        @NotNull
        @Override
        public FrameworkDetector getDetector()
        {
            return detector;
        }

        @Override
        public void setupFramework(@NotNull ModifiableModelsProvider modifiableModelsProvider, @NotNull ModulesProvider modulesProvider)
        {
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    final Module moduleForFile = ProjectRootManager.getInstance(context.getProject()).getFileIndex().getModuleForFile(collection.iterator().next());
                    final AddSupportForSingleFrameworkDialog dialog = AddSupportForSingleFrameworkDialog.createDialog(moduleForFile, new MuleFrameworkSupportProvider());
                    dialog.setTitle(dialog.getTitle() + " - " + moduleForFile.getName());
                    dialog.showAndGet();
                }
            });
        }

        @Override public boolean equals(Object o)
        {
            if (this == o)
                return true;
            MuleFrameworkDetected that = (MuleFrameworkDetected) o;

            if (o == null || getClass() != o.getClass())
                return false;

            if (!Objects.equals(detector, that.detector))
                return false;
            return Objects.equals(collection, that.collection);

        }

        @Override public int hashCode()
        {
            int result = detector != null ? detector.hashCode() : 0;
            result = 31 * result + (collection != null ? collection.hashCode() : 0);
            return result;
        }
    }
}
