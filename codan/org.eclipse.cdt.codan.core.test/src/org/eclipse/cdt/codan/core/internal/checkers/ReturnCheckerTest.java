/*******************************************************************************
 * Copyright (c) 2009, 2011 Alena Laskavaia
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Alena Laskavaia  - initial API and implementation
 *    Felipe Martinez  - ReturnCheckerTest implementation
 *******************************************************************************/
package org.eclipse.cdt.codan.core.internal.checkers;

import org.eclipse.cdt.codan.core.param.IProblemPreference;
import org.eclipse.cdt.codan.core.test.CheckerTestCase;
import org.eclipse.cdt.codan.internal.checkers.ReturnChecker;

/**
 * Test for {@see ReturnCheckerTest} class
 *
 */
public class ReturnCheckerTest extends CheckerTestCase {
	@Override
	public void setUp() throws Exception {
		super.setUp();
		enableProblems(ReturnChecker.RET_NORET_ID,ReturnChecker.RET_ERR_VALUE_ID,ReturnChecker.RET_NO_VALUE_ID);
	}
	//	dummy() {
	//	  return; // error here on line 2
	//	}
	public void testDummyFunction() {
		loadCodeAndRun(getAboveComment());
		checkNoErrors(); // because return type if not defined, usually people don't care
	}

	//	void void_function(void) {
	//	  return; // no error here
	//	}
	public void testVoidFunction() {
		loadCodeAndRun(getAboveComment());
		checkNoErrors();
	}

	//	int integer_return_function(void) {
	//	  if (global) {
	//		if (global == 100) {
	//			return; // error here on line 4
	//		}
	//	  }
	//	}
	public void testBasicTypeFunction() {
		loadCodeAndRun(getAboveComment());
		checkErrorLine(4);
	}

	//
	//	struct My_Struct {
	//	int a;
	//	};
	//
	//	 struct My_Struct struct_return_function(void) {
	//	return; // error here on line 6
	//	}
	public void testUserDefinedFunction() {
		loadCodeAndRun(getAboveComment());
		checkErrorLine(6);
	}

	//	 typedef unsigned int uint8_t;
	//
	//	uint8_t return_typedef(void) {
	//	return; // error here on line 4
	//	}
	public void testTypedefReturnFunction() {
		loadCodeAndRun(getAboveComment());
		checkErrorLine(4);
	}

	//	typedef unsigned int uint8_t;
	//
	//	uint8_t (*return_fp_no_typedef(void))(void)
	//	{
	//			return; // error here on line 5
	//	}
	public void testFunctionPointerReturnFunction() {
		loadCodeAndRun(getAboveComment());
		checkErrorLine(5);
	}

	//	void test() {
	//		  class A {
	//		   public:
	//		    void m() {
	//		      return; // should not be an error here
	//		    }
	//		  };
	//		}
	public void testInnerFunction_Bug315525() {
		loadCodeAndRunCpp(getAboveComment());
		checkNoErrors();
	}

	//	void test() {
	//		  class A {
	//		   public:
	//		    int m() {
	//		      return; // should be an error here
	//		    }
	//		  };
	//		}
	public void testInnerFunction_Bug316154() {
		loadCodeAndRunCpp(getAboveComment());
		checkErrorLine(5);
	}

	//	class c {
	//		c() {
	//			return 0;
	//		}
	//
	//		~c() {
	//			return;
	//		}
	//	};
	public void testContructorRetValue() {
		loadCodeAndRunCpp(getAboveComment());
		checkErrorLine(3, ReturnChecker.RET_ERR_VALUE_ID);
	}

	//	class c {
	//		c() {
	//			return;
	//		}
	//
	//		~c() {
	//			return;
	//		}
	//	};
	public void testContructor_Bug323602() {
		IProblemPreference macro = getPreference(ReturnChecker.RET_NO_VALUE_ID, ReturnChecker.PARAM_IMPLICIT);
		macro.setValue(Boolean.TRUE);
		loadCodeAndRunCpp(getAboveComment());
		checkNoErrors();
	}

	//	void f()
	//	{
	//	    [](int r){return r;}(5);
	//	}
	public void testLambda_Bug332285() {
		loadCodeAndRunCpp(getAboveComment());
		checkNoErrors();
	}
//	void f()
//	{
//	    if ([](int r){return r == 0;}(0))
//	        ;
//	}
	public void testLambda2_Bug332285() {
		loadCodeAndRunCpp(getAboveComment());
		checkNoErrors();
	}

	//	void g()
	//	{
	//		int r;
	//	    ({return r;});
	//	}
	public void testGccExtensions() {
		loadCodeAndRunCpp(getAboveComment());
		checkErrorLine(4);
	}

	//	auto f() -> void
	//	{
	//	}
	public void testVoidLateSpecifiedReturnType_Bug337677() {
		loadCodeAndRunCpp(getAboveComment());
		checkNoErrors();
	}

	//	auto f() -> void*
	//	{
	//	}
	public void testVoidPointerLateSpecifiedReturnType_Bug337677() {
		loadCodeAndRunCpp(getAboveComment());
		checkErrorLine(1);
	}
}