package sselab.switchy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sselab.switchy.macro.Define;
import sselab.switchy.macro.Include;

/**
 * The class is a preprocessor for 
 * @author SSELAB-129
 *
 */
public class SwitchyPreprocessor {
	
	private String fileName;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public SwitchyPreprocessor(String fileName) {
		this.fileName = fileName;
	}
	public SwitchyPreprocessor(){
		this.fileName = "";
	}
	
	private String readFile(){
		String str = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(new File(fileName)));
			String temp = null;
			//read until the end of the file
			while((temp = br.readLine()) != null){
				System.out.println(temp);
				str += temp + "\n";
				temp = null;
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str;
	}
	
	/**
	 * The method removes useless Declaration statements and Hook-routine functions.
	 * Also changes some strings to make it compilable.
	 */
	public void preprocess(){
		String fileContent = readFile();
		//Add a define macro to convert task call.
		fileContent = new Include("../model/os.h").getString() + fileContent;
		fileContent = new Define("TASK(t)", "TASK_##t()").getString() + fileContent;
		
		//remove Declare statement. (DeclareTask, DeclareResource, DeclareEvent)
		fileContent = fileContent.replace("Declare", "//Declare");
		//remove task_create statement, IT is not match the API of Ubinos model.
		fileContent = fileContent.replace("task_create", "//task_create");
			
		//Remove Hook-related function declarations. It is useless when checking with C_model and CBMC.
		Pattern pattern = Pattern.compile("void [a-z|A-Z|0-9| ]+Hook[ \n]*\\([a-z|A-Z|0-9| ]*\\)[\\n]*\\{[^\\}]+\\}");
	    Matcher matcher = pattern.matcher(fileContent);
	    while(matcher.find()){
	    	 fileContent = matcher.replaceAll("/*" + matcher.group() + "*/");
	    }
	    
	    //pattern = Pattern.compile("TASK\\([a-z|A-Z|0-9| ]+\\)[ \n]*\\([a-z|A-Z|0-9| ]*\\)[\\n]*\\{[^\\}]+\\}");
	    pattern = Pattern.compile("TASK\\([a-z|A-Z|0-9| ]+\\)[\n]*\\{");
	    matcher = pattern.matcher(fileContent);
	    StringBuffer sb = new StringBuffer();
	    while(matcher.find()){
	    	matcher.appendReplacement(sb, "void " + matcher.group());
	    }
	    matcher.appendTail(sb);
	    //System.out.println(sb);
	    fileContent = sb.toString();
	    writeProcessedFile(fileContent);
	}
	
	/**
	 * Write processed file contents into a new original_processed.c file.
	 * @param content
	 */
	private void writeProcessedFile(String content){
		String outFileName = fileName.replace(".c", "_processed.c");
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outFileName)));
			//bw.write("#define TASK(tid) TASK_##tid()\n");
			bw.write(content);
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
