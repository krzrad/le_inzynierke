/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
public class CassandraIndex {
    protected String name, tableName, identifier;
    String prepare(String input){
        String preparedInput = input.replace("CREATE","").replace("INDEX", "")
                .trim();
        if(preparedInput.contains("CUSTOM")){
            preparedInput = preparedInput.replace("CUSTOM","").trim();
        }
        return preparedInput;
    };
    void setName(String input){
        String preparedInput = prepare(input);
        String[] splitInput = preparedInput.split(" ");
        if(splitInput[0].equals("ON")){
            name = null;
        }else
            name = splitInput[0];
    };
    void setTableName(String input){
        String preparedInput = prepare(input);
        preparedInput = preparedInput.replaceAll(".+ON |ON ", "");
        String[] splitInput = preparedInput.split(" ");
        tableName = splitInput[0];
    };
    void setIdentifier(String input){
        String preparedInput = prepare(input);
        String[] splitInput = preparedInput.split(" ");
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(splitInput[splitInput.length-1]);
        while(m.find()){
            identifier = m.group(1);
        }
    };
}
