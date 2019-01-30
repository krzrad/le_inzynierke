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
        statement = input.substring(input.indexOf("SELECT"),input.indexOf("PRIMARY")).trim();
    }
    
    void setPrimaryKey(String input){;
        String byproduct = input.substring(input.indexOf("PRIMARY"));
        primaryKey = byproduct.substring(byproduct.indexOf("(")+1,byproduct.indexOf(")"));
    }
}
