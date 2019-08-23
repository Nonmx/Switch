package sselab.switchy;

import sselab.switchy.config.codegen.AlarmFileCodeGenerator;
import sselab.switchy.config.codegen.InitFileGenerator;

public class ConfigGenerator {

	public ConfigGenerator() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args){
		if(args.length < 1){
			System.out.println("Error! Give an OIL file please.");
		} else{
			InitFileGenerator.main(args);
			AlarmFileCodeGenerator.main(args);
		}
	}

}
