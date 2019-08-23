package sselab.switchy.config.codegen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.NamespaceContext;
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

import sselab.switchy.config.DynamicConfig;
import sselab.switchy.config.StaticConfig;

/**
 * The program to generate initialize.c and oil.h
 * @author SSELAB-129
 *
 */
public class XMLInitCodeGenerator implements CodeGenerator{
	private String code;
	private String fileName;
	private int taskNum;
	private int resourceNum;
	private int eventNum;
	private int maxPrio;
	private List<StaticConfig> staticConfigs=null;
	private List<DynamicConfig> dynamicConfigs=null;
	public XMLInitCodeGenerator(int taskNum, int resourceNum, int eventNum, int maxPrio){
		this.code = "";
		this.fileName = "";
		this.taskNum = taskNum;
		this.resourceNum = resourceNum;
		this.eventNum = eventNum;
		this.maxPrio = maxPrio;
		
		staticConfigs = new ArrayList<StaticConfig>();
		dynamicConfigs = new ArrayList<DynamicConfig>();
	}
	
	
	public XMLInitCodeGenerator(String fileName){
		this.code = "";
		this.fileName = fileName;
		staticConfigs = new ArrayList<StaticConfig>();
		dynamicConfigs = new ArrayList<DynamicConfig>();
		try {
			InputSource is = new InputSource(new FileReader("./"+fileName));
			Document document = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			//generate xpath
			 XPath  xpath = XPathFactory.newInstance().newXPath();
			 
			 //compile xml with expression keywords & get values from xml elements.
			 taskNum = getNum("task");
			 resourceNum = getNum("resource");
			 eventNum = getNum("event");
			// expression = "//*/configuration/resnum";
				// this.resourceNum = Integer.parseInt(xpath.compile(expression).evaluate(document));
				// expression = "//*/configuration/eventnum";
				// this.eventNum = Integer.parseInt(xpath.compile(expression).evaluate(document));
				// expression = "//*/configuration/maxprio";
				// this.maxPrio = Integer.parseInt(xpath.compile(expression).evaluate(document));
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void generateCode() {
		//add header first.
		code += "#include \"osek.h\"\n";
		//global array
		code += getArrayDeclaration();
		//Generate static config
		code += "void initialize(){\n";
			//initialize function generation
		code += getParsedTaskInfo();	
		code += getParsedAssignInfo();
		code += getDynamicStatInfo();
		code += getCeilingPrio();
		code += "}";
	}
	
	private String getCeilingPrio() {
		String code = "";
		for(int i = 0; i < resourceNum; i++){
			code += "\tCeiling_Prio["+i+"] = ";
			if(maxPrio == -1){
				code += "nondet_uchar();\n";
			}
			else{
				code += (maxPrio+1)+";\n";
			}
		}
		return code;
	}


	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	/**
	 * generate a code of global array declaration.
	 * @return
	 */
	
	private int getNum(String type){
		 String expression = "//*/"+type;
		 XPath  xpath = XPathFactory.newInstance().newXPath();
		 InputSource is = null;
		 NodeList list = null;
		 try {
			is = new InputSource(new FileReader("./"+fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			Document document = null;
			try {
				document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			} catch (SAXException | IOException | ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				list = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 return list.getLength();
	}
	
	private String getArrayDeclaration(){
		String code = "";
		code += getArray("task_static_config", "task_static_info", taskNum+1);
		code += getArray("task_dynamic_stat", "task_dyn_info", taskNum+1);
		code += getArray("unsigned char", "task_state", taskNum+1);
		code += getArray("unsigned char", "Ceiling_Prio", resourceNum);
		code += getArray("TaskList", "Resource_Table", resourceNum);
		code += getArray("TaskList", "Event_Table", eventNum);
		return code;
	}
	private String getArray(String type, String name, int size){
		return type + " " + name + "[" + size + "];\n";
	}
	
	
	/**
	 * parse xml file and make a configuration based on it.
	 * @param expression
	 */
	private String getParsedTaskInfo(){
		String code = "";
		String expression = "//*/task";
		InputSource is;
		try {
			is = new InputSource(new FileReader("./"+fileName));
			Document document = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			//generate xpath
			 XPath  xpath = XPathFactory.newInstance().newXPath();
			 NodeList tasks = (NodeList) xpath.compile(expression).evaluate(document, XPathConstants.NODESET);
			 for(int i = 0; i < tasks.getLength(); i++){
				 if(tasks.item(i).getNodeType() == Node.TEXT_NODE){
					 continue;
				 }
				 Node task = tasks.item(i);
				 NodeList taskInfoList = task.getChildNodes();
				 StaticConfig staticConfig = null;
				 int taskIdVal;
				 for(int j = 0; j < taskInfoList.getLength(); j++){
					 if(taskInfoList.item(j).getNodeType() == Node.TEXT_NODE)
						 continue;
					 else{
						 Node taskInfo = taskInfoList.item(j);
						 if(taskInfo.getNodeName().equals("taskid")){
							 taskIdVal = Integer.parseInt(taskInfo.getTextContent());
							 staticConfig = new StaticConfig(taskIdVal, "task_static_info");
						 }
						 else{
							 String attribute =
									 taskInfo.getAttributes().getNamedItem("name").getNodeValue();
							 NodeList fields = taskInfo.getChildNodes();
							 for(int k = 0; k < fields.getLength(); k++){
								 if(fields.item(k).getNodeType() == Node.ELEMENT_NODE){
									 String fieldName = "";
									 String fieldVal = "";
									 Node field = fields.item(k);
									 NodeList items = field.getChildNodes();
									 for(int l = 0; l < items.getLength(); l++){
										 Node item = items.item(l);
										 if(item.getNodeName().equals("name")){
											 fieldName =
													 item.getTextContent();
										 }
										 else if(item.getNodeName().equals("value")){
											 fieldVal = item.getTextContent();
										 }
									 }
									 staticConfig.addAttribute(fieldName, fieldVal);
									 //get fieldName to find out the maxPrio value.
									 if(fieldName.equals("prio")){
										 if(fieldVal.equals("AUTO")){
											 maxPrio = -1;
										 }
										 else{
											 int val = Integer.parseInt(fieldVal);
											 maxPrio = maxPrio != -1 && maxPrio < val ? val : maxPrio;
										 }
									 }
								 }
							 }
							 staticConfigs.add(staticConfig);
						 }
					 }
				 }
			 }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(StaticConfig s : staticConfigs){
			code += s.getConfigAssingments();
		}
		return code;
	}
	
	/**
	 * parse and get code string of <assign> nodes.
	 * @return String code
	 */
	@Deprecated
	private String getParsedAssignInfo(){
		String code = "";
		String expression = null;
		InputSource is;
		try {
			is = new InputSource(new FileReader("./"+fileName));
			Document document = 
					DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
			//generate xpath
			 XPath  xpath = XPathFactory.newInstance().newXPath();
			 
			 //get task_state
			 expression = "//*/assign/task_state";
			 NodeList assigns = 
					 (NodeList) xpath.compile(expression)
					 .evaluate(document, XPathConstants.NODESET);
			 for(int i = 0; i < assigns.getLength(); i++){
				 Node taskState = assigns.item(i);
				 String taskId=null;
				 String state = null;
				 for(int j = 0; j < taskState.getChildNodes().getLength(); j++){
					 Node item = taskState.getChildNodes().item(j);
					 if(item.getNodeName().equals("taskid")){
						 taskId = item.getTextContent();
					 }
					 else if(item.getNodeName().equals("state")){
						 state = item.getTextContent();
						 if(state.equals("AUTO")){
							 state = "nondet_uchar()";
						 }
					 }
				 }
				 code += "\t"+"task_state["+taskId+"]="+state+";\n";
			 }
			 //get ceil prio nodes
			 expression = "//*/assign/ceil_prio";
			 assigns = 
					 (NodeList) xpath.compile(expression)
					 .evaluate(document, XPathConstants.NODESET);
			 for(int i = 0; i < assigns.getLength(); i++){
				 Node taskState = assigns.item(i);
				 String resId=null;
				 String value = null;
				 for(int j = 0; j < taskState.getChildNodes().getLength(); j++){
					 Node item = taskState.getChildNodes().item(j);
					 if(item.getNodeName().equals("resid")){
						 resId = item.getTextContent();
					 }
					 else if(item.getNodeName().equals("value")){
						 value = item.getTextContent();
					 }
				 }
				 code += "\t"+"Ceiling_Prio["+resId+"]="+value+";\n";
			 }
		// TODO Auto-generated catch block
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return code;
	}
	
	private String getDynamicStatInfo(){
		String code = "";
		String arrName = "task_dyn_info";
		for(int i = 0; i < taskNum; i++){
			//assign static info to dynamic info.
			DynamicConfig dynConfig = new DynamicConfig(i+1, arrName);
			dynConfig.addAttribute("act_cnt", "0");
			String temp = "task_static_info["+(i+1)+"].prio";
			dynConfig.addAttribute("dyn_prio", temp);
			dynamicConfigs.add(dynConfig);
		}
		for(DynamicConfig d : dynamicConfigs){
			code += d.getConfigAssingments();
		}
		return code;
	}
}
