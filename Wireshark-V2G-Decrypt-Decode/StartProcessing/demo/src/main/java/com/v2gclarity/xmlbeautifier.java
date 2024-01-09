package com.v2gclarity;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class xmlbeautifier {
    private Logger logger = LogManager.getLogger(this.getClass().getSimpleName());

    public String prettyXMLPrinterByDom4j(String xmlString, int indent, boolean skipDeclaration) {
        String formattedXML = "";
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setIndentSize(indent);
            format.setSuppressDeclaration(skipDeclaration);
            format.setEncoding("UTF-8");
    
            org.dom4j.Document document = DocumentHelper.parseText(xmlString);
            StringWriter sw = new StringWriter();
            XMLWriter writer = new XMLWriter(sw, format);
            writer.write(document);
            formattedXML = sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
            getLogger().error("IOException occured while tring to beautify XML string: " + xmlString);
        } catch (DocumentException e) {
            e.printStackTrace();
            getLogger().error("DocumentException occured while tring to beautify XML string: " + xmlString);
        }

        return formattedXML;
    }
    public Logger getLogger() {
		return logger;
	}
}
