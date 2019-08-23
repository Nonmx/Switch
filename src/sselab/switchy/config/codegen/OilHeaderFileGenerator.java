package sselab.switchy.config.codegen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.internal.core.pdom.indexer.TodoTaskParser.Task;

import sselab.nusek.oil.InvalidOilException;
import sselab.nusek.oil.OilAlarm;
import sselab.nusek.oil.OilResource;
import sselab.nusek.oil.OilSpec;
import sselab.nusek.oil.OilTask;
import sselab.nusek.oil.file.OilLexer;
import sselab.nusek.oil.file.OilParser;
import sselab.switchy.config.DynamicConfig;
import sselab.switchy.config.StaticConfig;
import sselab.switchy.macro.Define;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
/**
 * The Class generates the code of oil.h from the original OIL file.
 * @author SSELAB-129
 *
 */
public class OilHeaderFileGenerator implements CodeGenerator{
	
	OilParser parser;
	String oilFileName;
	OilSpec spec;
	String code;
	int taskNum, resourceNum, eventNum, alarmNum;
	int maxPriority;
	private List<StaticConfig> staticConfigs=null;
	private List<DynamicConfig> dynamicConfigs=null;
	
	public OilHeaderFileGenerator() {
		// TODO Auto-generated constructor stub
	}
	public OilHeaderFileGenerator(String fileName) throws FileNotFoundException, InvalidOilException{
		OilLexer lexer = null;
		try {
			lexer = new OilLexer(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser = new OilParser(lexer);
		parser.parse();
		spec = parser.getOilSpec();
		code = "";
		taskNum = spec.getTasks().size();
		this.maxPriority = 0;
		for(OilTask t : spec.getTasks()){
			if(t.getPriority() > maxPriority)
			this.maxPriority = t.getPriority();
		}
		resourceNum = spec.getResources().size();
		for(OilResource r : spec.getResources()){
			if(r.getPrio() > maxPriority)
				maxPriority = r.getPrio();
		}
		eventNum = spec.getEvents().size();
		alarmNum = spec.getAlarms().size();
		staticConfigs = new ArrayList<StaticConfig>();
		dynamicConfigs = new ArrayList<DynamicConfig>();
	}

	@Override
	public void generateCode(){
		List<Define> def = new ArrayList<Define>();
		//Number of objects
		def.add(new Define("NUM_OF_TASKS", Integer.toString(taskNum+2)));
		def.add(new Define("MIN_TASK_ID", "0"));
		def.add(new Define("MAX_TASK_ID", Integer.toString(taskNum)));
		def.add(new Define("NUM_RESOURCES", Integer.toString(resourceNum+2)));
		def.add(new Define("MIN_RESOURCE_ID", "0"));
		def.add(new Define("MAX_RESOURCE_ID", Integer.toString(resourceNum)));
		def.add(new Define("NUM_EVENTS", Integer.toString(eventNum+2)));
		def.add(new Define("MIN_EVENT_ID", Integer.toString(0)));
		def.add(new Define("MAX_EVENT_ID", Integer.toString(eventNum)));
		def.add(new Define("NUM_OF_ALARMS", Integer.toString(alarmNum+2)));
		def.add(new Define("MAX_PRIORITY", Integer.toString(maxPriority)));
		//ID Mapping
		int idx = 1;
		def.add(new Define("Task0", Integer.toString(0))); //added task0 define 
		for(OilTask t : spec.getTasks()){
			def.add(new Define(t.getName(), Integer.toString(idx)));
			idx++;
		}
		idx = 1;
		for(OilAlarm a : spec.getAlarms()){
			def.add(new Define(a.getName(), Integer.toString(idx)));
			idx++;
		}
		
		//generate code here
		code += "#ifndef OIL_H_\n#define OIL_H_\n";
		for(Define d : def){
			code += d.getString();
		}
		code += "#endif\n";
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	public String getAppSrc(){
		return spec.getOs().getAppSrc();
	}

}
