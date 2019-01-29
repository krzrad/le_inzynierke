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
public class CassandraView {
    protected String name,statement,primaryKey;
    
    void setName(String input){
        String[] inputSplit = input.split(" ");
        name = inputSplit[3];
    }
    
    void setStatement(String input){
        /*String[] inputSplit = input.split(" ");
        statement = inputSplit[3];*/
    }
    
    void setPrimaryKey(String input){
        /*String[] inputSplit = input.split(" ");
        primaryKey = inputSplit[3];*/
    }
}
