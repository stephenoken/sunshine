package com.zooop.sunshine.data;

import android.test.AndroidTestCase;

/**
 * Created by stephenokennedy on 05/02/2016.
 */
public class TestPractice extends AndroidTestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public  void testThatDemonstratesAssertions() throws Throwable{
        int a =5,b = 3, c = 5, d = 10;

        assertEquals("X should be equeal", a, c);
        assertTrue("Y should be true", d > a);
        assertFalse("Z should be false", a ==b);

        if(b>d)
            fail("XX should never happen");
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
