package org.mule.tooling.lang.raml.util.xsd2raml;

import org.apache.xerces.dom.DOMXSImplementationSourceImpl;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.xs.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

public class Xsd2Raml {

    public static String URI_2001_SCHEMA_XSD = "http://www.w3.org/2001/XMLSchema";

    private int indentSize = 2;

    private XSNamedMap typeDefs;
    private XSModel xsModel;

    public Xsd2Raml(String xsdURI) {
        XSImplementation impl = (XSImplementation)
                (new DOMXSImplementationSourceImpl()).getDOMImplementation ("XS-Loader");
        XSLoader schemaLoader = impl.createXSLoader (null);
        this.xsModel = schemaLoader.loadURI(xsdURI);
        this.typeDefs = xsModel.getComponents(XSConstants.TYPE_DEFINITION);
    }

    public String getRamlHeader() {
        StringBuffer header = new StringBuffer();
        header.append("#%RAML 1.0 Library").append("\n");
        header.append("\ntypes:\n\n");
        return header.toString();
    }

    public String getRamlTypes() {
        StringBuffer ramlTypesBuffer = new StringBuffer();

        for (int idx = 0; idx < typeDefs.getLength(); idx++) {
            XSTypeDefinition nextType = (XSTypeDefinition)typeDefs.item(idx);
            //System.out.println("NEXT TYPE IS " + nextType);
            if (!URI_2001_SCHEMA_XSD.equals(nextType.getNamespace())) {
                if (nextType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
                    ramlTypesBuffer.append(doConvertSimpleType((XSSimpleType) nextType));
                } else {
                    ramlTypesBuffer.append(doConvertComplexType((XSComplexTypeDecl) nextType));
                }
            }
        }

        return ramlTypesBuffer.toString();
    }

    public String getRaml() {
        StringBuffer ramlBuffer = new StringBuffer();
        ramlBuffer.append(getRamlHeader());
        ramlBuffer.append(getRamlTypes());
        return ramlBuffer.toString();
    }

    //=================================================================================================================

    private String getIndent(int level) {
        StringBuffer indent = new StringBuffer();
        for (int l = 0; l < level; l++) {
            for (int i = 0; i < indentSize; i++)
                indent.append(" ");
        }
        return indent.toString();
    }

    private String doConvertSimpleType(XSSimpleType simpleType) {
        return doConvertSimpleType(simpleType, 0);
    }

    private String doConvertSimpleType(XSSimpleType simpleType, int indent) {
        StringBuffer ramlTypeBuffer = new StringBuffer();

        String typeName = simpleType.getName();
        if (typeName != null)
            ramlTypeBuffer.append(getIndent(indent + 1)).append(typeName).append(":").append("\n");

        String baseTypeName = simpleType.getBaseType().getName();

        String ramlType = ScalarType.forXsdType(baseTypeName);
        if (ramlType == null) {
            ramlType = baseTypeName;
        }

        if (simpleType.getBuiltInKind() == XSConstants.LIST_DT || simpleType.getBuiltInKind() == XSConstants.LISTOFUNION_DT) {
            ramlTypeBuffer.append(getIndent(indent + 2)).append("type: array").append("\n");
            ramlTypeBuffer.append(getIndent(indent + 2)).append("items: ").append("\n");

            ramlTypeBuffer.append(doConvertSimpleType((XSSimpleType)simpleType.getItemType(), indent + 1));
        } else {

            ramlTypeBuffer.append(getIndent(indent + 2)).append("type: ").append(ramlType).append("\n");

            String typeFacet = Facet.forXsdType(baseTypeName);
            if (typeFacet != null)
                ramlTypeBuffer.append(getIndent(indent + 2)).append(typeFacet).append("\n");

            StringList enumerations = simpleType.getLexicalEnumeration();
            if (enumerations.getLength() > 0) {
                ramlTypeBuffer.append(getIndent(indent + 2)).append("enum: [ ");
                ramlTypeBuffer.append(String.join(", ", (String[]) enumerations.toArray(new String[]{})));
                ramlTypeBuffer.append(" ]").append("\n");
            }

            XSObjectList facetList = simpleType.getFacets();
            for (int i = 0; i < facetList.getLength(); ++i) {
                XSFacet facet = (XSFacet) facetList.get(i);
                String ramlFacet = Facet.facetKindToString(facet.getFacetKind());
                if (ramlFacet != null)
                    ramlTypeBuffer.append(getIndent(indent + 2)).append(ramlFacet).append(": ").append(facet.getLexicalFacetValue()).append("\n");
            }

            ramlTypeBuffer.append("\n");
        }

        return ramlTypeBuffer.toString();
    }

    private String doConvertComplexType(XSComplexTypeDecl complexType) {
        return doConvertComplexType(complexType, 0);
    }

    private String doConvertComplexType(XSComplexTypeDecl complexType, int indent) {

        StringBuffer descriptionBuffer = new StringBuffer();

        XSObjectList annotations = complexType.getAnnotations();
        for (Object nextA : annotations) {
            XSAnnotation nextAnnotation = (XSAnnotation)nextA;

            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                nextAnnotation.writeAnnotation(doc, XSAnnotation.W3C_DOM_DOCUMENT);

                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList nl = (NodeList) xPath.evaluate("//*[local-name()='documentation']", doc.getDocumentElement(), XPathConstants.NODESET);

                for (int i = 0; i < nl.getLength(); i++) {
                    Node nextNode = nl.item(i);
                    if (nextNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element e = (Element) nextNode;
                        descriptionBuffer.append(e.getTextContent()).append(" ");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        StringBuffer ramlTypeBuffer = new StringBuffer();
        if (complexType.getName() != null)
            ramlTypeBuffer.append(getIndent(indent + 1)).append(complexType.getName()).append(":").append("\n");
        else
            indent = indent - 1; //No need for indentation for anonymous types

        ramlTypeBuffer.append(getIndent(indent + 2)).append("type: ");

        if ("anyType".equals(complexType.getBaseType().getName()))
            ramlTypeBuffer.append("object");
        else
            ramlTypeBuffer.append(complexType.getBaseType().getName());

        ramlTypeBuffer.append("\n");

        String description = descriptionBuffer.toString();
        if (!"".equals(description))
            ramlTypeBuffer.append(getIndent(indent + 2)).append("description: ").append(description).append("\n");

        ramlTypeBuffer.append(getIndent(indent + 2)).append("properties:").append("\n");

        XSParticle particle = complexType.getParticle();

        ramlTypeBuffer.append(particleToRaml(particle, indent + 2)).append("\n");

        return ramlTypeBuffer.toString();
    }

    private String particleToRaml(XSParticle particle, int indent) {
        return particleToRaml(particle, indent, false);
    }

    private String particleToRaml(XSParticle particle, int indent, boolean optional) {
        StringBuffer ramlBuffer = new StringBuffer();
        if (particle != null) {
            XSTerm term = particle.getTerm();

            if (term != null) {
                switch (term.getType()) {
                    case XSConstants.MODEL_GROUP: //this particle is a model group (one of <xs:sequence>, <xs:choice> or <xs:all>)
                        XSModelGroup mg = (XSModelGroup) term;
                        boolean isOptional = (mg.getCompositor() == XSModelGroup.COMPOSITOR_CHOICE);
                        //System.out.println(">>> COMPOSITOR : " + mg.getCompositor());

                        XSObjectList list = mg.getParticles();
                        for (int i = 0; i < list.getLength(); i++) {
                            XSParticle pp = (XSParticle) list.item(i);
                            ramlBuffer.append(particleToRaml(pp, indent + 1, isOptional));
                        }

                        break;
                    case XSConstants.ELEMENT_DECLARATION: //this particle is an element declaration (an <xs:element>)
                        XSElementDeclaration eDec = (XSElementDeclaration) term;

                        if (eDec.getName() != null)
                            ramlBuffer.append(getIndent(indent)).append(eDec.getName()).append(":\n");

                        XSObjectList annotations = eDec.getAnnotations();
                        for (Object nextA : annotations) {
                            XSAnnotation nextAnnotation = (XSAnnotation)nextA;
                        }
                        ramlBuffer.append(getIndent(indent + 1)).append("required: ").append(optional || (particle.getMinOccurs() > 0)).append("\n");

                        XSTypeDefinition eDecType = eDec.getTypeDefinition();

                        if (eDecType.getName() == null) { //This is anonymous type declaration:
                            if (eDecType.getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
                                ramlBuffer.append(doConvertSimpleType((XSSimpleType) eDecType, indent - 1));
                            } else {
                                ramlBuffer.append(doConvertComplexType((XSComplexTypeDecl) eDecType, indent));
                            }
                        } else {
                            ramlBuffer.append(getIndent(indent + 1)).append("type: ").append(eDecType.getName()).append("\n");
                        }

                        break;
                    case XSConstants.WILDCARD: //this particle is a wildcard (an <xs:any>)
                }
            } else {
                System.out.println("TERM IS NULL, PARTICLE IS " + particle.toString());
            }


        }
        return ramlBuffer.toString();
    }

    public static void main(String[] args) {
        Xsd2Raml xsd2raml = new Xsd2Raml(args[0]);
        System.out.println(xsd2raml.getRaml());

//        //Test Only
//        XSObjectList annotations = xsd2raml.xsModel.getAnnotations();
//        XSAnnotationImpl nextA = (XSAnnotationImpl)annotations.get(0);

    }

}
