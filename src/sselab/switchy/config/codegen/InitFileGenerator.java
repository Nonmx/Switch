package sselab.switchy.config.codegen;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sselab.nusek.oil.InvalidOilException;

/**
 * A main class of InitFileGenerator
 * @author SSELAB-129
 *
 */
public class InitFileGenerator {
	private static String INIT_CFILE_NAME = "./initialize.c";
	private static String OIL_HEADER_FILENAME = "./oil.h";
	//private static String configFileName = "*.oil"; 
	public static void main(String[] args){
		BufferedWriter bufferedWriter = null;
		CodeGenerator generator = null;
		if(args.length == 0 ){
			 generator = generateWithNoFile();
		}else{
			if(args[0].endsWith(".xml")){
				generator = new XMLInitCodeGenerator(args[0]);
				generator = generateWithXML(args[0]);
				System.out.println(generator.getCode());
				try {
					bufferedWriter = new BufferedWriter(new FileWriter(INIT_CFILE_NAME));
					bufferedWriter.write(generator.getCode());
					bufferedWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if(args[0].endsWith(".oil")){//Write to initilize.c file
				try {
					generator = new OilInitCodeGenerator(args[0]);	
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidOilException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				generator.generateCode();
				try {
					bufferedWriter = new BufferedWriter(new FileWriter(INIT_CFILE_NAME));
					bufferedWriter.write(generator.getCode());
					bufferedWriter.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//Write to oil.h file
				try {
					generator = new OilHeaderFileGenerator(args[0]);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidOilException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				generator.generateCode();
				try {
					bufferedWriter = new BufferedWriter(new FileWriter(OIL_HEADER_FILENAME));
					bufferedWriter.write(generator.getCode());
					bufferedWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				System.err.println("Wrong file format! Only xml and oil file can be used.");
				System.exit(1);
			}
		}
	}
	
	/**
	 * A method to generate InitFileCodeGenerator object & code without a file.
	 * @return InitFileCodeGenerator
	 */
	public static XMLInitCodeGenerator generateWithNoFile(){
		Scanner sc = new Scanner(System.in);
		System.out.println("Num of Tasks: ");
		int numOfTask = sc.nextInt();
		System.out.println("Num of Resources: ");
		int numOfRes = sc.nextInt();
		System.out.println("Num of Events: ");;
		int numOfEvent = sc.nextInt();
		System.out.println("Num of Max priority: ");
		int maxPrio = sc.nextInt();
		XMLInitCodeGenerator generator = 
				new XMLInitCodeGenerator(numOfTask, numOfRes, numOfEvent, maxPrio);
		generator.generateCode();
		sc.close();
		return generator;
		
	}
	
	/**
	 * The method to generate c file code with XML input. 
	 * @param fileName : name of XML configuration file.
	 * @return InitFileCodeGenerator
	 */
	public static XMLInitCodeGenerator generateWithXML(String fileName){
		
		XMLInitCodeGenerator generator = null;
		generator = new XMLInitCodeGenerator(fileName);
		generator.generateCode();
		return generator;
	}

}
