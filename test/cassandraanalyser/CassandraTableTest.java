/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import cassandraanalyser.CassandraTable.CassandraColumn;
import java.util.Arrays;
import java.util.List;
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
public class CassandraTableTest {
    
    public CassandraTableTest() {
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
     * Test of setName method, of class CassandraTable.
     */
    @Test
    public void testSetName() {
        String input = "CREATE TABLE table1 (a text PRIMARY KEY,b int,c float);";
        CassandraTable instance = new CassandraTable();
        instance.setName(input);
        assertEquals("nazwa tabeli źle ustawiona","table1", instance.name);
    }

    /**
     * Test of setColumns method, of class CassandraTable.
     */
    @Test
    public void testSetColumns() {
        String input = "CREATE TABLE table1 (a text PRIMARY KEY,b int,c float);";
        CassandraTable instance = new CassandraTable();
        instance.setColumns(input);
        CassandraColumn a,b,c;
        a = new CassandraColumn();
        a.name="a";
        a.type="text";
        a.properties="PRIMARY KEY";
        b = new CassandraColumn();
        b.name="b";
        b.type="int";
        c = new CassandraColumn();
        c.name="c";
        c.type="float";
        List<CassandraColumn> expResult = Arrays.asList(a,b,c);
        //assertThat(instance.columns,containsInAnyOrder(expResult)); jak dokonać tej assercji?!
    }
    
}
