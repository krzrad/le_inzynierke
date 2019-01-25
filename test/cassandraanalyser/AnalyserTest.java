/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author User
 */
public class AnalyserTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    
    @Before
    public void setUpStreams(){
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }
    
    @Test
    public void indexTest(){
        CassandraAnalyser analyserInstance = new CassandraAnalyser();
        String ref = "CREATE INDEX index1 ON table1 (col1);";
        String comp = "CREATE CUSTOM INDEX ON table2 (keys(col2));";
        String[] args = new String[3];
        args[0] = ref;
        args[1] = comp;
        analyserInstance.main(args);
        //assertEquals("bla", outContent.toString()); do ogarnięcia
    }
        
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    private void assertEquals(ByteArrayOutputStream outContent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
