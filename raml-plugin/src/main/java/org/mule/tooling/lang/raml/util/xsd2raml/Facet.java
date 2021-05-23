package org.mule.tooling.lang.raml.util.xsd2raml;

import org.apache.xerces.xs.XSSimpleTypeDefinition;

/**
 * Created by eberman on 5/19/17.
 */
public class Facet {

    public static String facetKindToString(short facetKind) {
        switch(facetKind) {
            case XSSimpleTypeDefinition.FACET_MINLENGTH: return "minLength";
            case XSSimpleTypeDefinition.FACET_MAXLENGTH: return "maxLength";
            case XSSimpleTypeDefinition.FACET_PATTERN: return "pattern";
            case XSSimpleTypeDefinition.FACET_MAXINCLUSIVE: return "maximum";
            case XSSimpleTypeDefinition.FACET_MININCLUSIVE: return "minimum";

//
//            case XSSimpleTypeDefinition.FACET_NONE: return "none";
//            case XSSimpleTypeDefinition.FACET_LENGTH: return "length";
//            case XSSimpleTypeDefinition.FACET_WHITESPACE: return "whitespace";
//            case XSSimpleTypeDefinition.FACET_MAXEXCLUSIVE: return "maxExclusive";
//            case XSSimpleTypeDefinition.FACET_MINEXCLUSIVE: return "minExclusive";
//            case XSSimpleTypeDefinition.FACET_TOTALDIGITS: return "totalDigits";
//            case XSSimpleTypeDefinition.FACET_FRACTIONDIGITS: return "fractionDigits";
//            case XSSimpleTypeDefinition.FACET_ENUMERATION: return "enumeration";
//            default: return "unknown facet kind";
            default: return null;
        }
    }

    public static String forXsdType(String xsdType) {
        if ("decimal".equals(xsdType)) return "format: double";
        if ("integer".equals(xsdType)) return "format: int";
        if ("long".equals(xsdType)) return "format: int64";
        if ("double".equals(xsdType)) return "format: double";

        return null;
    }

}
