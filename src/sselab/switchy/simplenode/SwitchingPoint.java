package sselab.switchy.simplenode;

public class SwitchingPoint {

	private ProgramCounter pc;
	
	public SwitchingPoint() {
	}
	
	public SwitchingPoint(String taskID, int pc){
		this.pc = new ProgramCounter(taskID, pc);
	}
	public SwitchingPoint(ProgramCounter pc){
		this.pc = pc;
	}
	
	public String getTaskID() {
		return pc.getTaskID();
	}

	public void setTaskID(String taskID) {
		pc.setTaskID(taskID);
	}

	public int getPcVal() {
		return pc.getPcVal();
	}

	public void setPcVal(int pcVal) {
		this.setPcVal(pcVal);
	}
	
	public String getCode(){
		String code = "";
		code += pc.getCode();
		code += "if(flag == 0)\n{\n scheduler();\nreturn;\n}\n";
		return code;
	}

}
