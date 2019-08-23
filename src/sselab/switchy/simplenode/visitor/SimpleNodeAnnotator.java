package sselab.switchy.simplenode.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import sselab.cfg.node.specifiednode.DeclarationNode;
import sselab.cfg.node.specifiednode.TailNode;
import sselab.nusek.csl.Csl;
import sselab.switchy.api.API;
import sselab.switchy.property.CSL;
import sselab.switchy.property.Property;
import sselab.switchy.property.factory.LocalAssertionFactory;
import sselab.switchy.property.factory.MonitoringCodeFactory;
import sselab.switchy.simplenode.Label;
import sselab.switchy.simplenode.ProgramCounter;
import sselab.switchy.simplenode.SimpleAPINode;
import sselab.switchy.simplenode.SimpleControlNode;
import sselab.switchy.simplenode.SimpleDeclarationNode;
import sselab.switchy.simplenode.SimpleLoopNode;
import sselab.switchy.simplenode.SimpleNode;
import sselab.switchy.simplenode.SimpleNormalNode;
import sselab.switchy.simplenode.SimplePseudoNode;
import sselab.switchy.simplenode.SwitchingPoint;

public class SimpleNodeAnnotator implements SimpleNodeVisitor{

	List<Csl> cslList = new ArrayList<Csl>();
	public List<Csl> getCslList() {
		return cslList;
	}

	public void setCslList(List<Csl> cslList) {
		this.cslList = cslList;
	}

	public SimpleNodeAnnotator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public int visit(SimpleNode node, Set<SimpleNode> visited) {
		return 0;
	}

	/**
	 * The method of visitor for APINode.
	 * Switching point is added.
	 * Properties are added.
	 */
	@Override
	public int visit(SimpleAPINode node, Set<SimpleNode> visited) {
		visit((SimpleNode)node, visited);
		//set switching point.
		if(node.getSwitchingPoint() == null){
		int val = node.getAPIKind().equals(API.TerminateTask)||
				node.getAPIKind().equals(API.ChainTask)? 0 : ProgramCounter.maxPCVal+1;
		SwitchingPoint point = new SwitchingPoint(node.getTaskID(), val);
		if(val > 0)
			ProgramCounter.maxPCVal++;
		node.setSwitchingPoint(point);
		}
		
		//add monitoring code.
		MonitoringCodeFactory factory = new MonitoringCodeFactory();
		factory.setTaskID(node.getTaskID());
		//assertions at the end.
		for(Csl csl : cslList){
			Property p = factory.createProperty(csl);
			if(p != null)
				node.addPostCondition(p);
		}		
		return 0;
	}
	/**
	 * visit method of LoopNode.
	 * Add a program counter saving,
	 * and after loop, there must be a save of program counter again. 
	 */
	@Override
	public int visit(SimpleLoopNode node, Set<SimpleNode> visited) {
		visit((SimpleNormalNode)node, visited);
		ProgramCounter pc = new ProgramCounter();
		pc.setTaskID(node.getTaskID());
		node.setStartPC(pc);
		return 0;
	}

	@Override
	public int visit(SimplePseudoNode node, Set<SimpleNode> visited) {
		visited.add(node);
		return 0;
	}

	@Override
	public int visit(SimpleDeclarationNode node, Set<SimpleNode> visited) {
		visited.add(node);
		if(!(node.getAfterNode(0) instanceof SimpleDeclarationNode)){
			node.setLabel(new Label(node.getTaskID(), 0));
			ProgramCounter.maxPCVal = 0;
		}
		return 0;
	}

	@Override
	public int visit(SimpleControlNode node, Set<SimpleNode> visited) {
		return visit((SimpleNormalNode)node, visited);
	}

	/**
	 * visit method for SimpleNormalNode.
	 * If the former node is LoopNode, program counter is updated.
	 * Otherwise, nothing is done.
	 */
	@Override
	public int visit(SimpleNormalNode node, Set<SimpleNode> visited) {
		visited.add(node);
		//If former node is a loop node, program counter is updated.
		//It is assumed that there are only 2 beforeNodes. (In actual code, there can be more.)
		if(node.isEndOfBlock() && node.getBeforeNodes().size() > 1 
				&& (node.getBeforeNode(1) instanceof SimpleLoopNode 
						|| node.getBeforeNode(0) instanceof SimpleLoopNode)){
			int idx = node.getBeforeNode(0) instanceof SimpleLoopNode ? 0 : 1;
				SimpleLoopNode loopNode = (SimpleLoopNode) node.getBeforeNode(idx);
			loopNode.setEndPC(new ProgramCounter(node.getTaskID(),++ProgramCounter.maxPCVal));
			loopNode.setLabel(new Label(node.getTaskID(), ProgramCounter.maxPCVal));
		}
		return 0;
	}

}
