package sselab.switchy.config.codegen;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
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
import sselab.nusek.oil.file.OilLexer;
import sselab.nusek.oil.file.OilParser;
import sselab.switchy.config.DynamicConfig;
import sselab.switchy.config.StaticConfig;
import sselab.switchy.macro.Include;

public class AlarmFileCodeGenerator implements CodeGenerator {
	private String oilFileName;
	private String code = "";
	private List<OilAlarm> alarms = null;
	private int alarmNum;

	public AlarmFileCodeGenerator() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String fileName = args[0];
		AlarmFileCodeGenerator generator = new AlarmFileCodeGenerator(fileName);
		generator.generateCode();
		
		//String code1 = generator.getCode();
		String code = generator.getCode();
		String code1 = generator.get_alarm_header();
		
		code += generator.getCodeHandler();
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("./alarm.c"));
			BufferedWriter bh = new BufferedWriter(new FileWriter("./alarm.h"));//added alarm.h 
			bh.write(code1);
			bh.close();
			bw.write(code);
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setOilFileName(String name) {
		this.oilFileName = name;
	}

	public String getOilFileName() {
		return this.oilFileName;
	}

	public AlarmFileCodeGenerator(String fileName) {
		try {
			alarms = parseOilFile(fileName);
		} catch (IOException | InvalidOilException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Parse OIL file and get information of each alarm.
	 * 
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	private List<OilAlarm> parseOilFile(String fileName)
			throws FileNotFoundException, IOException, InvalidOilException {
		OilLexer lexer = null;
		try {
			lexer = new OilLexer(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OilParser parser = new OilParser(lexer);
		parser.parse();
		OilSpec spec = parser.getOilSpec();
		code = "";
		this.alarmNum = spec.getAlarms().size();
		if (this.alarmNum > 0) {
			return spec.getAlarms();
		} else {
			return null;
		}
	}

	@Override
	public void generateCode() {
		// header file include code
		//code += new Include("model/alarm.h", Include.USER_DEFINED).getString();
		//code += new Include("model/oil.h", Include.USER_DEFINED).getString();
		//code += new Include("model/osek.h", Include.USER_DEFINED).getString();
		//code += new Include("stdlib.h", Include.STD_LIB).getString();
		//code += new Include("pthread.h", Include.STD_LIB).getString();
		  
		code += new Include("alarm.h",Include.USER_DEFINED).getString();
		code += new Include("oil.h",Include.USER_DEFINED).getString();
		code += new Include("osek.h",Include.USER_DEFINED).getString();
		code += new Include("time.h",Include.STD_LIB).getString();
		// data structures
		code += generateAlarmThreadCode(this.alarms);
	}

	public String generateAlarmThreadCode(List<OilAlarm> alarms) {
		String threadCode = "";

		for (OilAlarm a : alarms) {
			AlarmThreadCodeGenerator generator = new AlarmThreadCodeGenerator(a);
			generator.generateCode();
			threadCode += generator.getCode();
		}
		return threadCode;
	}

	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}
	
	public String getCodeHandler(){
		String code = "";
		code += "void handleAlarm(){\n";
		for(OilAlarm a : alarms){
			code += "\tif(alarm_state["+a.getId()+"] != CANCELED)\n";
			code += "\t\tALARM_"+a.getName()+"();\n";
		}
		code += "}\n";
		return code;
	}
	
	public String get_alarm_header() //alarm_header
	{
		AlarmThreadCodeGenerator alarm = new AlarmThreadCodeGenerator();
		String code = "";
		code+= "void handleAlarm();\n";
		//code+= "void ALARM_testalarm();\n";
		for(OilAlarm a : alarms)
		{
			code += "void ALARM_"+a.getName()+"();\n";
		}
		return code;
	}

}
