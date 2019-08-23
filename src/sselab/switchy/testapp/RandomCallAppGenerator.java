package sselab.switchy.testapp;

import sselab.switchy.config.codegen.CodeGenerator;

public class RandomCallAppGenerator implements CodeGenerator{
	int taskNum; //number of tasks
	int taskLength; //number of API calls in each task.
	String code = "";
	public RandomCallAppGenerator() {
		// TODO Auto-generated constructor stub
	}
	public RandomCallAppGenerator(int taskNum, int taskLength){
		this.taskNum = taskNum;
		this.taskLength = taskLength;
	}
	@Override
	public void generateCode() {
		
		for(int i = 1; i <= this.taskNum; i++){
			code += getTaskCode(i, taskLength);
		}
	}
	public String getTaskCode(int id, int length){
		APICallingCase apiCallingCase = new APICallingCase();
		String code = "";
		code += "void task" + id + "(){\n";
		code += "int tid = " + id + ";\n";
		code += "jump" + id + "();\n";
		apiCallingCase.generateCalls(id, length);
		code += apiCallingCase.getCode();
		code += "}\n\n";
		return code;
	}
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return this.code;
	}

}
