package sselab.switchy.simplenode;

public class Label {
	private String taskName;
	private int labelIndex;
	public Label() {
		
	}
	
	public Label(String taskName, int index){
		this.taskName = taskName;
		this.labelIndex = index;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public int getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(int labelIndex) {
		this.labelIndex = labelIndex;
	}
	
	public String getCode(){
		return "L_" + getTaskName() + "_" + getLabelIndex() + ":\n"; 
	}

}
