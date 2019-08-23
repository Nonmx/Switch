package sselab.switchy.config.codegen;

import sselab.nusek.oil.OilAlarm;
import sselab.nusek.oil.OilAlarmAction;

public class AlarmThreadCodeGenerator implements CodeGenerator{
	private String code = "";
	private String name;
	private OilAlarmAction action;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIncrement() {
		return increment;
	}
	public void setIncrement(int increment) {
		this.increment = increment;
	}
	public int getCycle() {
		return cycle;
	}
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}
	private int increment;
	private int cycle;
	
	public AlarmThreadCodeGenerator() {
	}
	
	public AlarmThreadCodeGenerator(String name, int increment, int cycle, 
			OilAlarmAction action){
		this.name = name;
		this.increment = increment;
		this.cycle = cycle;
		this.action = action;
	}
	
	public AlarmThreadCodeGenerator(OilAlarm alarm){
		this.name = alarm.getName();
		this.increment = alarm.getAlarmTime();
		this.cycle = alarm.getCycleTime();
		this.action = alarm.getAction();
	}
	
	@Override
	public void generateCode() {
		//name
		code += "void ALARM_" + this.name + "(){\n";
		code += "\ttime_t curtime,lasttime;\n";
		code += "\tlasttime = 0;\n";
		
		code += "\ttime(&curtime);\n";
		//wait until 'increment' milliseconds.
		code += "\tif(alarm_state["+this.name + "] == SET){\n";
		code += "\t\tif(difftime(curtime, lasttime) >= alarm_info["+this.name+"].increment){\n";
		switch(this.action.getAction()){
		case "ACTIVATETASK":
			code += "\t\tActivateTask(" + this.action.getTask() + ");\n";
			break;
		case "SETEVENT":
			code += "\t\tSetEvent(" + this.action.getEvent() +", " + this.action.getTask() + ");\n";
			break;
		case "ALARMCALLBACK":
			code += this.action.getAlarmCallBack() + "();";
			break;
		}
		code += "\t\ttime(&lasttime);\n";
		code += "\t\talarm_state["+this.name+"] = STARTED;\n";
		code += "\t\t}\n\t}\n";
		code += "\telse if(alarm_state["+this.name+"] == STARTED){\n";
		code += "\t\tif(alarm_info["+this.name+"].cycle == 0){\n\t\t\talarm_state["+this.name+"] = CANCELED;\n\t\t}\n";
		switch(this.action.getAction()){
		case "ACTIVATETASK":
			code += "\t\tActivateTask(" + this.action.getTask() + ");\n";
			break;
		case "SETEVENT":
			code += "\t\tSetEvent(" + this.action.getEvent() +", " + this.action.getTask() + ");\n";
			break;
		case "ALARMCALLBACK":
			code += this.action.getAlarmCallBack() + "();";
			break;
		}
		code += "\t\ttime(&lasttime);\n";
		code += "\t}\n";
		code += "\telse\n\t\treturn;\n";
		code += "}\n";
		
		
	}
	/*
	public void generateCode() {
		//name
		code += "void* Alarm_" + this.name + "(void* args){\n";
		code += "\tAlarm* arg = (Alarm*)args;\n";
		//wait until 'increment' milliseconds.
		code += "\tsleep(arg->increment);\n";
		code += "\twhile(alarm_state[" + this.name +"] != 0){\n" ;
		
		switch(this.action.getAction()){
		case "ACTIVATETASK":
			code += "\t\tActivateTask(" + this.action.getTask() + ");\n";
			break;
		case "SETEVENT":
			code += "\t\tSetEvent(" + this.action.getEvent() +", " + this.action.getTask() + ");\n";
			break;
		case "ALARMCALLBACK":
			code += this.action.getAlarmCallBack() + "();";
			break;
		}
		code += "\t}\n"
				+ "\tif(arg->cycle == 0) break;\n"
				+ "\tsleep(arg->cycle);\n"
				+ "\tpthread_exit(0);\n}\n";
	}*/
	
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}
	

}
