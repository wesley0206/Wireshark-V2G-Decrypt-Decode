/*******************************************************************************
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2015 - 2019  Dr. Marc Mültin (V2G Clarity)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 *******************************************************************************/
package com.v2gclarity.risev2g.shared.exiCodec;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.openexi.scomp.EXISchemaFactory;

import com.v2gclarity.risev2g.shared.enumerations.GlobalValues;

/**
 * This class is needed to access all linked XSD files via an InputStream. Accessing XSD files
 * via an InputStream is needed when using them inside a jar file. Normally, if a XSD file imports  
 * other XSD files, an exception will be thrown because those files cannot be found (since there
 * is no file system to search at, just an InputStream). With this XSDResolver, every imported
 * XSD file must be added together with its InputStream to a Map of entities the GrammarLoader (EXIficient)
 * or Transmogrifier (OpenExi) can use for lookup. 
 */
public class XSDResolver implements XMLEntityResolver {

	private static final XSDResolver instance = new XSDResolver();
	private HashMap<String, XMLInputSource> xmlInputSourceEntities;
	
	private XSDResolver() {
		EXISchemaFactory exiSchemaFactory = new EXISchemaFactory();
		EXISchemaFactoryExceptionHandler esfe = new EXISchemaFactoryExceptionHandler();
		exiSchemaFactory.setCompilerErrorHandler(esfe);
		
		InputStream isV2GCIAC = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_AC.toString());
		XMLInputSource xmlISV2GCIAC = new XMLInputSource(null, null, null, isV2GCIAC, null);
		
		InputStream isV2GCIACDP = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_ACDP.toString());
		XMLInputSource xmlISV2GCIACDP = new XMLInputSource(null, null, null, isV2GCIACDP, null);
		
		InputStream isV2GCICommonMessages = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_CommonMessages.toString());
		XMLInputSource xmlISV2GCICommonMessages = new XMLInputSource(null, null, null, isV2GCICommonMessages, null);
		
		InputStream isV2GCICommonTypes = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_CommonTypes.toString());
		XMLInputSource xmlISV2GCICommonTypes = new XMLInputSource(null, null, null, isV2GCICommonTypes, null);
		
		InputStream isV2GCIDC = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_DC.toString());
		XMLInputSource xmlISV2GCIDC = new XMLInputSource(null, null, null, isV2GCIDC, null);

		InputStream isV2GCIWPT = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_WPT.toString());
		XMLInputSource xmlISV2GCIWPT = new XMLInputSource(null, null, null, isV2GCIWPT, null);

		InputStream isXMLDSig = getClass().getResourceAsStream(GlobalValues.SCHEMA_PATH_XMLDSIG.toString());
		XMLInputSource xmlISXMLDSig = new XMLInputSource(null, null, null, isXMLDSig, null);
		
		setEntity("V2G_CI_AC.xsd", xmlISV2GCIAC);
		setEntity("V2G_CI_ACDP.xsd", xmlISV2GCIACDP);
		setEntity("V2G_CI_CommonMessage.xsd", xmlISV2GCICommonMessages);
		setEntity("V2G_CI_CommonTypes.xsd", xmlISV2GCICommonTypes);
		setEntity("V2G_CI_DC.xsd", xmlISV2GCIDC);
		setEntity("V2G_CI_WPT.xsd", xmlISV2GCIWPT);
		setEntity("xmldsig-core-schema.xsd", xmlISXMLDSig);
	}
	
	
	public static XSDResolver getInstance() {
		return instance;
	}
	
	
	public void setEntity(String literalSystemId, XMLInputSource xmlInput) {
		if (xmlInputSourceEntities == null) {
			xmlInputSourceEntities = new HashMap<String, XMLInputSource>();
		}
		
		xmlInputSourceEntities.put(literalSystemId, xmlInput);
	}


	@Override
	public XMLInputSource resolveEntity(XMLResourceIdentifier resourceIdentifier) 
			throws XNIException, IOException {
		String literalSystemId = resourceIdentifier.getLiteralSystemId(); 
		
		if (xmlInputSourceEntities != null && xmlInputSourceEntities.containsKey(literalSystemId)) {
			return xmlInputSourceEntities.get(literalSystemId);
		}

		return null;
	}
}
