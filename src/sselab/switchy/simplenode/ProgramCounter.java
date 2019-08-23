package sselab.switchy.simplenode;

public class ProgramCounter {

	private String taskID;
	private int pcVal;
	
	public static int maxPCVal = 0;
	
	public ProgramCounter() {
		// TODO Auto-generated constructor stub
	}
	
	public ProgramCounter(String taskID){
		setTaskID(taskID);
	}
	
	public ProgramCounter(String taskID, int pcVal){
		setTaskID(taskID);
		setPcVal(pcVal);
		
	}
	
	/* setters and getters */
	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public int getPcVal() {
		return pcVal;
	}

	public void setPcVal(int pcVal) {
		this.pcVal = pcVal;
		if(this.pcVal > ProgramCounter.maxPCVal){
			ProgramCounter.maxPCVal = this.pcVal;
		}
	}
	
	public String getCode(){
		String str = "";
		if(taskID == null)
			setTaskID("main");
		str += "current_pc[" + taskID + "] = " + pcVal + ";\n";
		return str;
	}
	
	

}
