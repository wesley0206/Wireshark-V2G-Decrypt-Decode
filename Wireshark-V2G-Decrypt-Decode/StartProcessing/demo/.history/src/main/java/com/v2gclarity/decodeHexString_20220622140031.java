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
package com.v2gclarity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.grammars.Grammars;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.v2gclarity.risev2g.shared.exiCodec.EXIficient_V2G_CI_AppProtocol;
import com.v2gclarity.risev2g.shared.exiCodec.EXIficient_V2G_CI_MsgDef;
//import com.v2gclarity.risev2g.shared.utils.ByteUtils;
import com.v2gclarity.risev2g.shared.utils.MiscUtils;
import com.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolReq;
import com.v2gclarity.risev2g.shared.v2gMessages.appProtocol.SupportedAppProtocolRes;
import com.v2gclarity.risev2g.shared.v2gMessages.msgDef.V2GMessage;

public class decodeHexString {
    private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());
    private Marshaller marshaller;
	private Unmarshaller unmarshaller;
	private InputStream inStream;
	private boolean xmlMsgRepresentation;
    private EXIFactory exiFactory;
	private Grammars grammarAppProtocol;
	private Grammars grammarMsgDef;
    private String decodedExi;
    private JAXBContext jaxbContext;
    private boolean isSupportedAppProtocolReqFound = false;
    private boolean isSupportedAppProtocolResFound = false;


    public synchronized void setJaxbContext(@SuppressWarnings("rawtypes") Class... classesToBeBound) {
		try {
			setJaxbContext(JAXBContext.newInstance(classesToBeBound));
			
			// Every time we set the JAXBContext, we need to also set the marshaller and unmarshaller for EXICodec
			setUnmarshaller(getJaxbContext().createUnmarshaller());
			setMarshaller(getJaxbContext().createMarshaller());
			
			/*
			 * JAXB by default silently ignores errors. Adding this code to throw an exception if 
			 * something goes wrong.
			 */
            
			getUnmarshaller().setEventHandler(new ValidationEventHandler() {
				        @Override
						public boolean handleEvent(ValidationEvent event ) {
				            throw new RuntimeException(event.getMessage(),
				                                       event.getLinkedException());
				        }
				});
		} catch (JAXBException e) {
			getLogger().error("A JAXBException occurred while trying to set JAXB context", e);
		}
	}

    public decodeHexString() {
        // Setting the JAXBContext is a very time-consuming action and should only be done once during startup
		setJaxbContext(SupportedAppProtocolReq.class, SupportedAppProtocolRes.class, V2GMessage.class);

		// Check if XML representation of sent messages is to be shown (for debug purposes)
		if ((boolean) MiscUtils.getPropertyValue("exi.messages.showxml")) 
			setXMLMsgRepresentation(true);
		else
			setXMLMsgRepresentation(false);
            setExiFactory(DefaultEXIFactory.newInstance());
            
        /*
        * The Java classes
        * - EXIficient_V2G_CI_AppProtocol,
        * - EXIficient_V2G_CI_MsgDef, and 
        * - EXIficient_xmldsig_core_schema
        * are serialized versions of the respective XSD schema files.
        * These serializations have been created using a tool available at
        * https://github.com/EXIficient/exificient-grammars/blob/master/src/main/java/com/siemens/ct/exi/grammars/persistency/Grammars2JavaSourceCode.java
        */
        setGrammarAppProtocol(new EXIficient_V2G_CI_AppProtocol());
        setGrammarMsgDef(new EXIficient_V2G_CI_MsgDef());
        
        // Non-default settings to fulfill requirements [V2G2-099] and [V2G2-600]
        getExiFactory().setValuePartitionCapacity(0);
        getExiFactory().setMaximumNumberOfBuiltInElementGrammars(0);
        getExiFactory().setMaximumNumberOfBuiltInProductions(0);
	}

    public synchronized Object exiToSuppAppProtocolMsg(byte[] exiEncodedMessage) {
		return decodeEXI(exiEncodedMessage, true);
	}

    public synchronized Object exiToV2gMsg(byte[] exiEncodedMessage) {
		return decodeEXI(exiEncodedMessage, false);
	}

    public synchronized Object decodeEXI(byte[] exiEncodedMessage, boolean supportedAppProtocolHandshake) {
		//getLogger().debug("Received EXI stream: " + ByteUtils.toHexString(exiEncodedMessage));
		
		ByteArrayInputStream bais = new ByteArrayInputStream(exiEncodedMessage);
		setDecodedExi(decode(bais, supportedAppProtocolHandshake));
		
		return unmarshallToMessage(getDecodedExi());
	}
    
    public Object unmarshallToMessage(String decodedExiString) {
		try {
			if (getInStream() != null) getInStream().reset();
			setInStream(new ByteArrayInputStream(decodedExiString.getBytes()));
			Object unmarhalledObject = getUnmarshaller().unmarshal(getInStream());
			
			if (isXMLMsgRepresentation()) showXMLRepresentationOfMessage(unmarhalledObject);
			return unmarhalledObject;
		} catch (IOException | JAXBException | RuntimeException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to unmarshall decoded message", e);
			return null;
		}
	}

    private synchronized String decode(InputStream exiInputStream, boolean supportedAppProtocolHandshake) {
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = null;
		StringWriter stringWriter = new StringWriter();

		try {
			transformer = tf.newTransformer();
		} catch (TransformerConfigurationException e) {
			getLogger().error("Error occurred while trying to decode (TransformerConfigurationException)", e);
		}
		
		if (supportedAppProtocolHandshake) exiFactory.setGrammars(grammarAppProtocol);
		else exiFactory.setGrammars(grammarMsgDef);
		
		try {
			EXISource saxSource = new EXISource(exiFactory);
			SAXSource exiSource = new SAXSource(new InputSource(exiInputStream));
			XMLReader exiReader = saxSource.getXMLReader();
			exiSource.setXMLReader(exiReader);
			transformer.transform(exiSource, new StreamResult(stringWriter));
		} catch (EXIException e) {
			getLogger().error("Error occurred while trying to decode (EXIException)", e);
		} catch (TransformerException e) {
			getLogger().error("Error occurred while trying to decode (TransformerException)", e);
		}
		
		return stringWriter.toString();
	}

    /**
	 * Shows the XML representation of a marshalled or unmarshalled message object. This is useful for debugging
	 * purposes.
	 * 
	 * @param message The (un)marshalled message object
	 */
	@SuppressWarnings("rawtypes")
	public void showXMLRepresentationOfMessage(Object message) {
		StringWriter sw = new StringWriter();
		String className = "";
		
		if (message instanceof V2GMessage) {
			className = ((V2GMessage) message).getBody().getBodyElement().getName().getLocalPart();
			if(className == "SessionStopRes") {
				setSupportedAppProtocolReqFound(false);
				setSupportedAppProtocolResFound(true);
			}
		} else if (message instanceof JAXBElement) {
			className = ((JAXBElement) message).getName().getLocalPart();
		} else if (message instanceof SupportedAppProtocolReq) {
			className = "SupportedAppProtocolReq";
            setSupportedAppProtocolReqFound(true);
		} else if (message instanceof SupportedAppProtocolRes) {
			className = "SupportedAppProtocolRes";
            setSupportedAppProtocolResFound(true);
		} else {
			className = "marshalled JAXBElement";
		}
		
		try {
			getMarshaller().marshal(message, sw);
			xmlbeautifier xb = new xmlbeautifier();
			getLogger().info("XML representation of " + className + ": ");
			getLogger().info(xb.prettyXMLPrinterByDom4j(sw.toString(), 4, true));
		} catch (JAXBException e) {
			getLogger().error(e.getClass().getSimpleName() + " occurred while trying to show XML representation of " + className, e);
		}
	}

    public Logger getLogger() {
		return logger;
	}
    public JAXBContext getJaxbContext() {
		return jaxbContext;
	}
    private void setJaxbContext(JAXBContext jaxbContext) {
		this.jaxbContext = jaxbContext;
	}
    public InputStream getInStream() {
		return inStream;
	}
    public EXIFactory getExiFactory() {
		return exiFactory;
	}
    private void setExiFactory(EXIFactory exiFactory) {
		this.exiFactory = exiFactory;
	}
    private void setGrammarAppProtocol(Grammars grammarAppProtocol) {
		this.grammarAppProtocol = grammarAppProtocol;
	}
    private void setGrammarMsgDef(Grammars grammarMsgDef) {
		this.grammarMsgDef = grammarMsgDef;
	}
    public void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}
    public void setDecodedExi(String decodedExi) {
		this.decodedExi = decodedExi;
	}
    public String getDecodedExi() {
		return decodedExi;
	}
    public Marshaller getMarshaller() {
		return marshaller;
	}
    public void setMarshaller(Marshaller marshaller) {
		this.marshaller = marshaller;
	}
    public Unmarshaller getUnmarshaller() {
		return unmarshaller;
	}
    public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
    public boolean isXMLMsgRepresentation() {
		return xmlMsgRepresentation;
	}
    private void setXMLMsgRepresentation(boolean xmlMsgRepresentation) {
		this.xmlMsgRepresentation = xmlMsgRepresentation;
	}
    public boolean getSupportedAppProtocolReqFound() {
        return isSupportedAppProtocolReqFound;
    }
    private void setSupportedAppProtocolReqFound(boolean isFound) {
        isSupportedAppProtocolReqFound = isFound;
    }
    public boolean getSupportedAppProtocolResFound() {
        return isSupportedAppProtocolResFound;
    }
    private void setSupportedAppProtocolResFound(boolean isFound) {
        isSupportedAppProtocolResFound = isFound;
    }
}
