/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author User
 */
public class CassandraAnalyserTest {
    
    public CassandraAnalyserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class CassandraAnalyser.
     */
    @Test
    public void testMain() {
        String[] args = new String[2];
        args[0] = "/root/a.cql";
        args[1] = "/root/b.cql";
        CassandraAnalyser.main(args);
    }
    
}
