package test;

import static org.junit.Assert.*;

import org.junit.Test;

import sselab.switchy.simplenode.ProgramCounter;
import sselab.switchy.simplenode.SwitchingPoint;

public class SwitchingPointTest {

	@Test
	public void test() {
		SwitchingPoint point = new SwitchingPoint(new ProgramCounter("t2", 7));
		point.getCode().equals("pc[t2]=7;\nif(flag) return;\nL_t2_7:\n");
	}

}
