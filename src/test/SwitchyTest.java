package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import sselab.cfg.parser.CFGNode;
import sselab.cfg.parser.FunctionCFG;
import sselab.switchy.Switchy;
import sselab.switchy.simplenode.SimpleNode;

public class SwitchyTest {

	 SimpleNode[] nodes = null;
	 FunctionCFG[] cfg = null;
	private  void initialize(){
		Switchy switchy = new Switchy("test2.c");
		cfg = switchy.getCodeAntFunctionCFG();
		nodes = switchy.getModifiableFunctionCFG();
	}
	
	@Test
	public void test() {
		initialize();
		int idx = 0;
		for(SimpleNode node : nodes){
			ArrayList<SimpleNode> queue= new ArrayList<SimpleNode>();
			ArrayList<CFGNode> queue2= new ArrayList<CFGNode>();
			queue.add(node);
			queue2.add(cfg[idx].getHeadNode());
			while(!queue.isEmpty()){
				assertEquals(queue.get(0).getStringVal(), queue2.get(0).getCode());
				if(queue.get(0).getAfterNodes() != null){
					for(SimpleNode n : queue.get(0).getAfterNodes()){
						queue.add(n);
					}
				}
				if(queue2.get(0).getNextNodes() != null){
					for(CFGNode n : queue2.get(0).getNextNodes()){
						queue2.add(n);
					}
				}
				queue.remove(0);
				queue2.remove(0);
			}
			idx++;
		}
	}
	
	

}
