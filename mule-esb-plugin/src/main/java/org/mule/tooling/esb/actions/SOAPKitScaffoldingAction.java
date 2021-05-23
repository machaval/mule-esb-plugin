package org.mule.tooling.esb.actions;

import com.intellij.icons.AllIcons;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.apache.commons.io.IOUtils;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.mule.soapkit.common.utils.WsdlUtils;
import org.mule.soapkit.xml.generator.Scaffolder;
import org.mule.tooling.esb.launcher.configuration.project.MuleDeployProperties;
import org.mule.tooling.esb.soapkit.ui.SoapKitDialog;
import org.mule.tooling.esb.util.MuleConfigUtils;
import org.mule.tooling.esb.util.MuleUIUtils;

import javax.wsdl.Definition;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class SOAPKitScaffoldingAction extends AnAction
{
    private static final Scaffolder SCAFFOLDER = Scaffolder.getInstance();

    final static Logger logger = Logger.getInstance(SOAPKitScaffoldingAction.class);

    public SOAPKitScaffoldingAction()
    {
        super("Generate Flows", "Generate APIKit for SOAP scaffolding from the WSDL definition", AllIcons.FileTypes.WsdlFile);
        //logger.setLevel(Level.DEBUG);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final VirtualFile selectedWsdlFile = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        final Project project = anActionEvent.getProject();
        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);

        final VirtualFile moduleContentRoot = ProjectRootManager.getInstance(project).getFileIndex().getContentRootForFile(selectedWsdlFile);
        final VirtualFile appsRoot = moduleContentRoot.findFileByRelativePath(MuleConfigUtils.CONFIG_RELATIVE_PATH);
        String appPath = appsRoot.getPath();

        String wsdlPath = selectedWsdlFile.getPath();

        logger.debug("*** WSDL PATH IS " + wsdlPath);
        logger.debug("*** APP PATH IS " + appPath);

        Definition wsdlDefinition = WsdlUtils.parseWSDL(wsdlPath);

        List<String> configFiles = MuleDeployProperties.getConfigFileNames(appPath);

        final SoapKitDialog form = new SoapKitDialog(wsdlDefinition, configFiles);
        form.show();
        boolean isOk = form.getExitCode() == DialogWrapper.OK_EXIT_CODE;

        if (!isOk)
            return;

        final String service = form.getService().getSelectedItem().toString();
        final String port = form.getPort().getSelectedItem().toString();
        String muleXml = form.getConfigFile().getSelectedItem().toString();

        logger.debug("*** SERVICE " + service);
        logger.debug("*** PORT " + port);
        logger.debug("*** muleXml " + muleXml);
        logger.debug("*** wsdlPath " + wsdlPath);

        if (SoapKitDialog.NEW_FILE.equals(muleXml)) {
            File muleXmlFile = null;
            int i = 0;

            do {
                muleXml = selectedWsdlFile.getNameWithoutExtension() + (i > 0 ? "-" + i : "") + ".xml";
                muleXmlFile = new File(appPath, muleXml);
                i++;
            } while (muleXmlFile.exists());
        }

        final String muleXmlConfigFileName = muleXml;

        try {
            new WriteCommandAction.Simple(project, psiFile) {
                @Override
                protected void run() throws Throwable {
                    VirtualFile vMuleXmlFile = appsRoot.findOrCreateChildData(this, muleXmlConfigFileName);
                    Element resultElement = SCAFFOLDER.scaffold(wsdlPath, wsdlPath, service, port, "");
                    writeMuleXmlFile(resultElement, vMuleXmlFile);
                }
            }.execute();
        } catch (Exception e) {
            Notification notification = MuleUIUtils.MULE_NOTIFICATION_GROUP.createNotification("Unable to generate flows from WSDL File",
                    "Error Message : " + e, NotificationType.ERROR, null);
            Notifications.Bus.notify(notification, project);
        }
        moduleContentRoot.refresh(false, true);
    }

    @Override
    public void update(AnActionEvent anActionEvent)
    {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());
        final boolean isWSDL;
        if (file != null)
        {
            isWSDL = "wsdl".equalsIgnoreCase(file.getExtension());
            anActionEvent.getPresentation().setEnabled(isWSDL);
            anActionEvent.getPresentation().setVisible(isWSDL);
        }

    }
/*
    private SoapkitApiConfig findSoapkitConfig(File muleConfig) {
        SoapkitApiConfig soapkitApiConfig = null;
        if (muleConfig.exists()) {
            try (InputStream contents = new FileInputStream(muleConfig)) {
                List<SoapkitApiConfig> soapkitConfigs = new SoapkitMuleConfigParser(contents).soapkitConfigs();
                if (!soapkitConfigs.isEmpty()) {
                    soapkitApiConfig = soapkitConfigs.get(0);
                }
            } catch (Exception e) {
                logger.error("Problem parsing config file: " + muleConfig.getAbsolutePath(), e);
            }
        }
        return soapkitApiConfig;
    }
*/
    private void writeMuleXmlFile(Element element, VirtualFile muleConfig) {

        XMLOutputter xout = new XMLOutputter(Format.getPrettyFormat());
        InputStream inputStream = null;
        try {
            String outputString = xout.outputString(element);
            logger.debug("*** OUTPUT STRING IS " + outputString);
            OutputStreamWriter writer = new OutputStreamWriter(muleConfig.getOutputStream(this));
            writer.write(outputString);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            logger.error("Unable to write file: " + e);
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
