package sselab.switchy.testapp;

import java.util.ArrayList;
import java.util.List;

import sselab.switchy.config.codegen.CodeGenerator;
import sselab.switchy.property.Parameter;
import sselab.switchy.property.Property;

public class UnitCheckGenerator implements CodeGenerator {
	private String APIName;
	private List<Parameter> params;
	private List<String> localVars;
	private List<Property> preConditions;
	private List<Property> postConditions;
	String code = "";
	
	public UnitCheckGenerator(String APIName) {
		this.APIName = APIName;
		this.params = new ArrayList<Parameter>();
		this.localVars = new ArrayList<String>();
		this.preConditions = new ArrayList<Property>();
		this.postConditions = new ArrayList<Property>();
	}
			
	@Override
	public void generateCode() {
		code += generateMacro();
		code += generatePrimitives();
		//name of the function = APIName_verify()
		code += "void "+this.APIName + 
				"_verify()";
		code += "\n{\n";
		code += generateVarDeclaration();
		code += generatePreConditions();
		code += generateAPICall();
		code += generatePostConditions();
		code += "}\n";
		code += "void app()\n{\n\t"+APIName+"_verify();"+"\n}\n";
	}
	@Override
	public String getCode() {
		// TODO Auto-generated method stub
		return code;
	}
	
	public void addParam(String name, String min, String max){
		Parameter param = 
				new Parameter(name, min, max);
		this.params.add(param);
	}
	
	public void addProperty(Property property){
		if(property.getLocation() == Property.PRE){
			this.preConditions.add(property);
		}else{
			this.postConditions.add(property);
		}
	}
	public void addLocalVar(String name){
		this.localVars.add(name);
	}
	
	public List<Property> getPreConditions(){
		return this.preConditions;
	}
	
	public List<Property> getPostConditions(){
		return this.postConditions;
	}
	
	private String generateVarDeclaration(){
		String code = "";
		for(Parameter p : params){
			code += "\tunsigned char "+ p.getName() + " = nondet_uchar();\n";
		}
		for(String l : localVars){
			code += "\tunsigned char "+ l + ";\n";
		}
		//return value of the API is saved in this variable.
		code += "\tint ret=0;\n"; 
		return code;
	}
	
	private String generatePreConditions(){
		String code = "";
		for(Parameter p : params){
			String min = p.getMinVal();
			String max = p.getMaxVal();
			if(min == null && max == null) break;
			
			code += "\t__CPROVER_assume(";
			if(min != null)
				code += p.getName() + " >= " + min;
			if(max != null){
				if(min != null) code += " && ";
				code += p.getName() + " <= " + max;
			}
			code += ");\n";
		}
		for(Property p : preConditions){
			code += "\t" + p.getStatement() + "\n";
		}
		return code;
	}
	
	private String generateAPICall(){
		String code = "";
		code += "\tret = " + this.APIName + "(";
		for(Parameter p : params){
			code += p.getName();
			if(params.indexOf(p) != params.size() - 1){
				code += ", ";
			}
		}
		code += ");\n";
		return code;
	}
	
	private String generatePostConditions(){
		String code = "";
		for(Property p : postConditions){
			code += "\t" + p.getStatement() + "\n";
		}
		return code;
	}
	
	/**
	 * generates macro of test app file.
	 * @return String code
	 */
	private String generateMacro(){
		String code="";
		code += "#include <assert.h>\n";
		code += "#include \"osek.h\"\n";
		code += "#include \"oil.h\"\n";
		code += "#include \"readyQ.h\"\n";
		code += "#include \"kernel.h\"\n";
		return code;
	}
	
	private String generatePrimitives(){
		String code = "";
		code += "extern unsigned char nondet_uchar();\n";
		code += "void app();\n";
		return code;
	}
}
