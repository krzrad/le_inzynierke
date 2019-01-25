/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
public class CassandraTable {
    protected String name;

    public CassandraTable() {
        this.columns = new ArrayList<>();
    }
    protected static class CassandraColumn{
        protected String name,type,properties;
        void setName(String input){
            String[] inputSplit = input.split(" ");
            name = inputSplit[0];
        };
        void setType(String input){
            String[] inputSplit = input.split(" ");
            type = inputSplit[1];
        };
        void setProperties(String input){
            String[] inputSplit = input.split(" ");
            if(inputSplit.length>=3)
                properties = inputSplit[2];
            else properties = null;
        };
    };
        
    protected List<CassandraColumn> columns;

    void setName(String input){
        String[] inputSplit = input.split(" ");
        name = inputSplit[2];
    }

    void setColumns(String input){
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(input);
        while(m.find()) {
            String[] splitColumns = m.group(1).split(",");
            for (String splitColumn : splitColumns) {
                CassandraColumn column = new CassandraColumn();
                column.setName(splitColumn);
                column.setType(splitColumn);
                column.setProperties(splitColumn);
                columns.add(column);
            }
        }
    }
}
