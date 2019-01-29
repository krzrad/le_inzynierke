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
public class CassandraIndexTest {
    
    public CassandraIndexTest() {
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
     * Test of prepare method, of class CassandraIndex.
     */
    @org.junit.Test
    public void testPrepare() {
        String input = "CREATE CUSTOM INDEX index1 ON table1 (sth)";
        CassandraIndex instance = new CassandraIndex();
        String expResult = "index1 ON table1 (sth)";
        String result = instance.prepare(input);
        assertEquals("ten index nie został przygotowany",expResult, result);
    }

    /**
     * Test of setName method, of class CassandraIndex.
     */
    @org.junit.Test
    public void testSetName() {
        String input = "index1 ON table1 (sth)";
        String expResult = "index1";
        CassandraIndex instance = new CassandraIndex();
        instance.setName(input);
        assertEquals("nazwa została źle ustawiona",expResult,instance.name);
    }

    /**
     * Test of setTableName method, of class CassandraIndex.
     */
    @org.junit.Test
    public void testSetTableName() {
        String input = "index1 ON table1 (sth)";
        String expResult = "table1";
        CassandraIndex instance = new CassandraIndex();
        instance.setTableName(input);
        assertEquals("nazwa tabeli została źle ustawiona",expResult,instance.tableName);
    }

    /**
     * Test of setIdentifier method, of class CassandraIndex.
     */
    @org.junit.Test
    public void testSetIdentifier() {
        String input = "index1 ON table1 (sth)";
        String expResult = "sth";
        CassandraIndex instance = new CassandraIndex();
        instance.setIdentifier(input);
        assertEquals("identyfikator został źle ustawiony",expResult,instance.identifier);
    }
    
}
