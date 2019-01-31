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
        args[0] = "CREATE TABLE table1 (\n"
                + "moviename text PRIMARYKEY,\n"
                + "ticketPrice float,\n"
                + "rentID int);\n"
                + "\n"
                + "CREATE TABLE table2 (\n"
                + "uid int PRIMARYKEY,\n"
                + "password text);\n"
                + "\n"
                + "CREATE CUSTOM INDEX index1 ON table1 (moviename);\n"
                + "CREATE INDEX index2 ON table2 (ticketPrice);";
        args[1] = "CREATE TABLE table1 (\n"
                + "moviename text PRIMARYKEY,\n"
                + "rentID uuid,\n"
                + "expDate text);\n"
                + "\n"
                + "CREATE TABLE table3 (\n"
                + "id text PRIMARYKEY,\n"
                + "highScore int);\n"
                + "\n"
                + "CREATE CUSTOM INDEX index1 ON table1 (rentID);\n"
                + "CREATE INDEX ON table3 (keys(highScore));";
        CassandraAnalyser.main(args);
    }
    
}
