package sselab.switchy.simplenode;

import java.util.Set;

import sselab.cfg.node.specifiednode.DeclarationNode;
import sselab.cfg.parser.CFGNode;
import sselab.switchy.simplenode.visitor.SimpleNodePrinter;
import sselab.switchy.simplenode.visitor.SimpleNodeVisitor;

public class SimpleDeclarationNode extends SimpleNormalNode{
	private String varName;
	private String varVal;
	
	public SimpleDeclarationNode() {
		// TODO Auto-generated constructor stub
	}
	public SimpleDeclarationNode(CFGNode node, String taskID){
		super(node, taskID);
		String code = node.getCode().trim();
		String[] tokens = code.split("=");
		varName = tokens[0];
		if(tokens.length > 1)
			varVal = tokens[1].substring(0, tokens[1].length());
		else
			varVal = "nondet_int()"; //nondeterministic value.
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getVarVal() {
		return varVal;
	}
	public void setVarVal(String varVal) {
		this.varVal = varVal;
	}
	
	@Override
	public void accept(SimpleNodeVisitor visitor, Set<SimpleNode> visited) {
		if(visited.contains(this))
			return;
		visitor.visit(this, visited);
		if(this.isEndOfBlock())
			((SimpleNodePrinter)visitor).endOfBlock();
		getAfterNode(0).accept(visitor, visited);
	}
	
}
