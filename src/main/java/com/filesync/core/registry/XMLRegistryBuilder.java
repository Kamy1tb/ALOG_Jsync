package com.filesync.core.registry;

import com.filesync.core.Profile;
import com.filesync.core.Registry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * Implementation of RegistryBuilder for XML format.
 */
public class XMLRegistryBuilder implements RegistryBuilder {
    
    @Override
    public Registry buildRegistry(String filePath, Profile profile) throws IOException {
        Registry registry = new Registry();
        File file = new File(filePath);
        
        if (!file.exists()) {
            return registry;
        }
        
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);
            document.getDocumentElement().normalize();
            
            NodeList entryList = document.getElementsByTagName("entry");
            
            for (int i = 0; i < entryList.getLength(); i++) {
                Node node = entryList.item(i);
                
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String path = element.getAttribute("path");
                    long lastModified = Long.parseLong(element.getAttribute("lastModified"));
                    
                    registry.addEntry(path, lastModified);
                }
            }
        } catch (ParserConfigurationException | SAXException e) {
            throw new IOException("Error parsing XML registry: " + e.getMessage(), e);
        }
        
        return registry;
    }
    
    @Override
    public void saveRegistry(Registry registry, String filePath, Profile profile) throws IOException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            
            // Create root element
            Element rootElement = document.createElement("registry");
            rootElement.setAttribute("profileName", profile.getName());
            document.appendChild(rootElement);
            
            // Add entries
            for (Registry.Entry entry : registry.getEntries()) {
                Element entryElement = document.createElement("entry");
                entryElement.setAttribute("path", entry.getPath());
                entryElement.setAttribute("lastModified", String.valueOf(entry.getLastModified()));
                rootElement.appendChild(entryElement);
            }
            
            // Write to file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
            
        } catch (ParserConfigurationException | TransformerException e) {
            throw new IOException("Error saving XML registry: " + e.getMessage(), e);
        }
    }
}
