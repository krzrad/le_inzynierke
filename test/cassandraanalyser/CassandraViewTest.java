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
public class CassandraViewTest {
    
    public CassandraViewTest() {
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
     * Test of setName method, of class CassandraView.
     */
    @Test
    public void testSetName() {
        String input = "CREATE MATERIALIZED VIEW view1 AS SELECT (qty, id, condition) FROM WHERE condition > 2 PRIMARY KEY (id)";
        CassandraView instance = new CassandraView();
        instance.setName(input);
        String expResult = "view1";
        assertEquals("nazwa została źle ustawiona",expResult, instance.name);
    }

    /**
     * Test of setStatement method, of class CassandraView.
     */
    @Test
    public void testSetStatement() {
        String input = "CREATE MATERIALIZED VIEW view1 AS SELECT (qty, id, condition) FROM WHERE condition > 2 PRIMARY KEY (id)";
        CassandraView instance = new CassandraView();
        instance.setStatement(input);
        String expResult = "SELECT (qty, id, condition) FROM WHERE condition > 2";
        assertEquals("statement został źle ustawiony",expResult, instance.statement);
    }

    /**
     * Test of setPrimaryKey method, of class CassandraView.
     */
    @Test
    public void testSetPrimaryKey() {
        String input = "CREATE MATERIALIZED VIEW view1 AS SELECT (qty, id, condition) FROM WHERE condition > 2 PRIMARY KEY (id)";
        CassandraView instance = new CassandraView();
        instance.setPrimaryKey(input);
        String expResult = "id";
        assertEquals("primary key został źle ustawiony",expResult, instance.primaryKey);
    }
    
}
