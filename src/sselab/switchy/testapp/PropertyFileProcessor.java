package sselab.switchy.testapp;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sselab.switchy.config.codegen.CodeGenerator;
import sselab.switchy.property.Assertion;
import sselab.switchy.property.Assumption;
import sselab.switchy.property.Property;
import sselab.switchy.property.StmtProperty;

public class PropertyFileProcessor{
	private String fileName = "";
	
	public PropertyFileProcessor(String filename){
		this.fileName = filename;
	}
	
	
	public void generateAppFiles() {
		//add header file
		String code = "";
		InputSource is;
		try {
			is = new InputSource(new FileReader("./"+fileName));
			Document document = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			//generate xpath
			 XPath  xpath = XPathFactory.newInstance().newXPath();
			 
			 //get task_state
			 String expression = "//*/api";
			 NodeList nodes = 
					 (NodeList) xpath.compile(expression)
					 .evaluate(document, XPathConstants.NODESET);
			 for(int i = 0; i < nodes.getLength(); i++){
				 if(nodes.item(i).getNodeType() == Node.ELEMENT_NODE){
					 code = makeUnitDriver(nodes.item(i)).getCode();
					 String fileName = 
							 nodes.item(i).getAttributes().getNamedItem("name")
							 .getNodeValue() + "_test.c";
					 try {
						 	BufferedWriter bufferedWriter =
						 			new BufferedWriter(new FileWriter(fileName));
							bufferedWriter.write(code);
							bufferedWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 }
			 }
		} catch (SAXException | IOException | ParserConfigurationException 
				| XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private UnitCheckGenerator makeUnitDriver(Node node){
		//node is a node of api info.
		String apiname = 
				node.getAttributes().getNamedItem("name").getNodeValue();
		UnitCheckGenerator unitCheckGenerator = new UnitCheckGenerator(apiname);
		NodeList children = node.getChildNodes();
		for(int i = 0; i < children.getLength(); i++){
			Node childNode = children.item(i);
			String nodeName = childNode.getNodeName();
			if(nodeName.equals("param")){
				Node paramNode=null, minNode=null, maxNode=null;
				paramNode = childNode.getAttributes().getNamedItem("name");
				minNode = childNode.getAttributes().getNamedItem("min");
				maxNode = childNode.getAttributes().getNamedItem("max");
				String paramName=null, minVal=null, maxVal=null;
				if(paramNode != null){
					paramName = paramNode.getNodeValue();
				}
				if(minNode != null){
					minVal = minNode.getNodeValue(); 
				}if(maxNode != null){
					maxVal = maxNode.getNodeValue(); 
				}
				unitCheckGenerator.addParam(paramName, minVal, maxVal);
				
			}else if(nodeName.equals("temp")){
				String varName = 
						childNode.getAttributes().getNamedItem("name").getNodeValue();
				unitCheckGenerator.addLocalVar(varName);
			}else if(nodeName.equals("pre")){
				NodeList propertyNodes = childNode.getChildNodes();
				for(int j = 0; j < propertyNodes.getLength(); j++){
					String propertyVal;
					Node propertyNode = propertyNodes.item(j);
					//pass null string values
					if(propertyNode.getNodeType() == Node.TEXT_NODE)
						continue;
					propertyVal = propertyNode.getTextContent();
					//System.out.println(propertyVal);
					if(propertyNode.getNodeName().equals("assume")){
						unitCheckGenerator.addProperty(
								new Assumption(propertyVal, Property.PRE));
					}else if(propertyNode.getNodeName().equals("assert")){
						unitCheckGenerator.addProperty(
								new Assertion(propertyVal, Property.PRE));
					}else{
						unitCheckGenerator.addProperty(
								new StmtProperty(propertyVal, Property.PRE));
					}
				}
			}else if(nodeName.equals("post")){
				NodeList propertyNodes = childNode.getChildNodes();
				for(int j = 0; j < propertyNodes.getLength(); j++){
					String propertyVal;
					Node propertyNode = propertyNodes.item(j);
					//pass null string values
					if(propertyNode.getNodeType() == Node.TEXT_NODE)
						continue;
					propertyVal = propertyNode.getTextContent();
					if(propertyNode.getNodeName().equals("assume")){
						unitCheckGenerator.addProperty(
								new Assumption(propertyVal, Property.POST));
					}else if(propertyNode.getNodeName().equals("assert")){
						unitCheckGenerator.addProperty(
								new Assertion(propertyVal, Property.POST));
					}else{
						unitCheckGenerator.addProperty(
								new StmtProperty(propertyVal, Property.POST));
					}
				}
			}
		}
		unitCheckGenerator.generateCode();
		System.out.println(unitCheckGenerator.getCode());
		return unitCheckGenerator;
	}

	
	
	
	
}
