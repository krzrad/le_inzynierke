/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

/**
 *
 * @author User
 */
public class CassandraTrigger {
    protected String name, table, logicFile;
    
    void setName(String input){
        String[] inputSplit = input.split(" ");
        name = inputSplit[2];
    }
    
    void setTable(String input){
        table = input.substring(input.indexOf("ON"),input.indexOf("USING")).replace("ON","").trim();
    }
    
    void setLogicFile(String input){
        String[] inputSplit = input.split(" ");
        logicFile = inputSplit[inputSplit.length-1];
    }
}
