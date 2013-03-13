package org.movshovich.msaver.test;

import org.movshovich.msaver.ExpensesFragment;

import junit.framework.TestCase;

public class ExpensesFragmentTest extends TestCase {
	private ExpensesFragment ef = new ExpensesFragment();
	public void testAddDot() {
		assertEquals("-0.03", ef.addingDotToString("-3"));
		assertEquals("-0.20", ef.addingDotToString("-20"));
		assertEquals("-2.23", ef.addingDotToString("-223"));
		assertEquals("0", ef.addingDotToString("0"));
		assertEquals("0.03", ef.addingDotToString("3"));
		assertEquals("0.20", ef.addingDotToString("20"));
		assertEquals("2.23", ef.addingDotToString("223"));
	}
}
