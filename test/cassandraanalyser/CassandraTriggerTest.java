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
public class CassandraTriggerTest {
    
    public CassandraTriggerTest() {
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
     * Test of setName method, of class CassandraTrigger.
     */
    @Test
    public void testSetName() {
        String input = "CREATE TRIGGER tr1 ON table1 USING trigger.doing.something";
        CassandraTrigger instance = new CassandraTrigger();
        instance.setName(input);
        assertEquals("nazwa została źle ustawiona","tr1", instance.name);
    }

    /**
     * Test of setTable method, of class CassandraTrigger.
     */
    @Test
    public void testSetTable() {
        String input = "CREATE TRIGGER tr1 ON table1 USING trigger.doing.something";
        CassandraTrigger instance = new CassandraTrigger();
        instance.setTable(input);
        assertEquals("nazwa tabeli została źle ustawiona","table1", instance.table);
    }

    /**
     * Test of setLogicFile method, of class CassandraTrigger.
     */
    @Test
    public void testSetLogicFile() {
        String input = "CREATE TRIGGER tr1 ON table1 USING trigger.doing.something";
        CassandraTrigger instance = new CassandraTrigger();
        instance.setLogicFile(input);
        assertEquals("bilioteka wyzwalacza została źle ustawiona","trigger.doing.something", instance.logicFile);
    }
    
}
