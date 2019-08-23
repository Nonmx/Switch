package sselab.switchy.simplenode;

import java.util.Set;

import sselab.cfg.node.BranchNode;
import sselab.cfg.parser.CFGNode;
import sselab.switchy.simplenode.visitor.SimpleNodePrinter;
import sselab.switchy.simplenode.visitor.SimpleNodeVisitor;

/**
 * Class of Loop Nodes (for, while) 
 * @author SSELAB-129
 *
 */
public class SimpleLoopNode extends SimpleNormalNode{

	private ProgramCounter startPC;
	private ProgramCounter endPC;
	public SimpleLoopNode() {
		// TODO Auto-generated constructor stub
	}
	
	public SimpleLoopNode(CFGNode node, String taskID){
		super(node, taskID);
		this.setStringVal(node.getCode());
	}

	/**
	 * The method returns pc at the start of a loop. 
	 * @return startPC
	 */
	public ProgramCounter getStartPC() {
		return startPC;
	}

	/**
	 * The method sets the value of startPC.
	 * @param startPC
	 */
	public void setStartPC(ProgramCounter startPC) {
		this.startPC = startPC;
	}

	/**
	 * The method returns pc at the end of a loop.
	 * @return endPC
	 */
	public ProgramCounter getEndPC() {
		return endPC;
	}

	/**
	 * The method sets the value of endPC.
	 * @param endPC
	 */
	public void setEndPC(ProgramCounter endPC) {
		this.endPC = endPC;
	}
	@Override
	public void accept(SimpleNodeVisitor visitor, Set<SimpleNode> visited) {
		if(visited.contains(this))
			return;
		visitor.visit(this, visited);
		getAfterNode(0).accept(visitor, visited);
		if(this.isEndOfBlock()){
			if(visitor instanceof SimpleNodePrinter)
				((SimpleNodePrinter)visitor).endOfBlock();
		}
		getAfterNode(1).accept(visitor, visited);
	}
	
	@Override
	public String getCode(){
		String code = "";
		code += getStringVal();
		code += "{\n";
		if(!getTaskID().equals("main"))
			code += this.startPC.getCode();
		return code;
	}
}
