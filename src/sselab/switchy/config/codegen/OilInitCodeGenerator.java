package sselab.switchy.config.codegen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import sselab.nusek.oil.InvalidOilException;
import sselab.nusek.oil.OilAlarm;
import sselab.nusek.oil.OilSpec;
import sselab.nusek.oil.OilTask;
import sselab.nusek.oil.file.OilLexer;
import sselab.nusek.oil.file.OilParser;
import sselab.switchy.config.DynamicConfig;
import sselab.switchy.config.StaticConfig;

/**
 * A class to generate Oil.h header file.
 * @author SSELAB-129
 *
 */
public class OilInitCodeGenerator implements CodeGenerator{
	OilParser parser;
	String oilFileName;
	OilSpec spec;
	String code;
	int maxPrio = 0;
	int taskNum, resourceNum, eventNum, alarmNum;
	private List<StaticConfig> staticConfigs=null;
	private List<DynamicConfig> dynamicConfigs=null;
	
	public OilInitCodeGenerator(String fileName) throws FileNotFoundException, InvalidOilException{
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
		resourceNum = spec.getResources().size();
		eventNum = spec.getEvents().size();
		alarmNum = spec.getAlarms().size();
		staticConfigs = new ArrayList<StaticConfig>();
		dynamicConfigs = new ArrayList<DynamicConfig>();
	}
	
	@Override
	public void generateCode() {
		code += "#include \"osek.h\"\n";
		//global array
		code += getArrayDeclaration();
		//Generate static config
		code += "void initialize(){\n";
		code += TASK0_info();
		code += getParsedTaskInfo();
		code += task0_dynamic_info();
		code += getDynamicStatInfo();
		code += getParsedAlarmInfo();
		code += getCeilingPrio();
		code += "}";
	}

	private String getCeilingPrio() {
		String code = "";
		for(int i = 0; i < spec.getResources().size(); i++){
			code += "\tCeiling_Prio["+i+"] = " + (maxPrio+1) + ";\n";
			maxPrio++;
		}
		return code;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
	
	private String TASK0_info()//add task0 information into the initialize.c
	{
		//List<OilTask> Task;
		String code = "";
		
		String prio_of_task0 = Integer.toString(0); //add task0 infomation in initialize.c 
		String activation_of_task0 = Integer.toString(1);
		String isAutoStart_of_task0 = Integer.toString(1);
		String isExtended_of_task0 = Integer.toString(0);
		String isPreemptive_of_task0 = Integer.toString(1);
		StaticConfig config_of_task0 = new StaticConfig(0,"task_static_info");
		config_of_task0.addAttribute("prio", prio_of_task0);
		//maxPrio = maxPrio < Task.getPriority()? Task.getPriority() : maxPrio;
		config_of_task0.addAttribute("autostart", isAutoStart_of_task0);
		config_of_task0.addAttribute("extented", isExtended_of_task0);
		config_of_task0.addAttribute("max_act_cnt", activation_of_task0);
		config_of_task0.addAttribute("preemptable", isPreemptive_of_task0);
		staticConfigs.add(config_of_task0);
		

		return code;
		
	}
	
	private String getParsedTaskInfo(){
		String code = "";
		List<OilTask> tasks = spec.getTasks();
		int id = 1;
		for(OilTask task : tasks){
			String prio = Integer.toString(task.getPriority());
			String activation = Integer.toString(task.getActivation());
			String isAutoStart = task.isAutostart() ? "1" : "0";
			String isExtended = task.isExtended() ? "1" : "0";
			String isPreemptive = task.isSchedule() ? "1" : "0";
			StaticConfig config = new StaticConfig(id, "task_static_info");
			config.addAttribute("prio", prio);
			maxPrio = maxPrio < task.getPriority() ? task.getPriority() : maxPrio;
			config.addAttribute("autostart", isAutoStart);
			config.addAttribute("extended", isExtended);
			config.addAttribute("max_act_cnt", activation);
			config.addAttribute("preemptable", isPreemptive);
			staticConfigs.add(config);
			id++;
		}
		for(StaticConfig s : staticConfigs){
			code += s.getConfigAssingments();
		}
		return code;
	}
	
	private String getParsedAlarmInfo(){
		String code = "";
		List<OilAlarm> alarms = spec.getAlarms();
		for(OilAlarm a : alarms){
			if(a.isAutostart())
				code += "\talarm_state["+a.getName()+"] = SET;\n";
			else
				code += "\talarm_state["+a.getName()+"] = CANCELED;\n";
			code += "\talarm_info["+a.getName()+"].increment = " + a.getAlarmTime()+";\n";
			code += "\talarm_info["+a.getName()+"].cycle = " + a.getCycleTime()+";\n";
		}
		return code;
	}

	/**
	 * generate a code of global array declaration.
	 * @return
	 */
	private String getArrayDeclaration(){
		String code = "";
		code += getArray("task_static_config", "task_static_info", taskNum+2);
		code += getArray("task_dynamic_stat", "task_dyn_info", taskNum+2);
		code += getArray("unsigned char", "task_state", taskNum+2);
		code += getArray("unsigned char", "Ceiling_Prio", resourceNum+2);
		code += getArray("Resource", "Resource_Table", resourceNum+2);
		code += getArray("Event", "Event_Table", eventNum+2);
		code += getArray("Alarm", "alarm_info", alarmNum+2);
		code += getArray("int", "alarm_state", alarmNum+2);
		
		return code;
	}
	
	private String getArray(String type, String name, int size){
		return type + " " + name + "[" + size + "];\n";
	}
	
	private String task0_dynamic_info()
	{
		int num = 0;
		String code = "";
		String arrName = "task_dyn_info";
		DynamicConfig dynConfig = new DynamicConfig(0,arrName);
		dynConfig.addAttribute("act_cnt","0");
		String temp = "task_static_info["+ num +"].prio";
		dynConfig.addAttribute("dyn_prio",temp);
		dynamicConfigs.add(dynConfig);
		
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
