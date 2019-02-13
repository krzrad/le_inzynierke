/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
public class CassandraTable {
    protected String name;
    protected boolean hasPrimaryKey = false; 

    public CassandraTable() {
        this.columns = new ArrayList<>();
    }

    private void lookForPrimaryKey(String input) {
        //System.out.println("wejście: "+input);
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
                properties = input.substring(input.indexOf(inputSplit[2]));
            else properties = null;
        };
    };
        
    protected List<CassandraColumn> columns;

    void setName(String input){
        Pattern tablePattern = Pattern.compile("\\bCREATE\\b\\s+\\bTABLE\\b",Pattern.CASE_INSENSITIVE);
        String output = tablePattern.matcher(input).replaceAll("");
        output = output.substring(0,output.indexOf("(")).trim();
        name = output;
    }

    void setColumns(String input){
        Pattern p = Pattern.compile("\\((.*?)\\)");
        Matcher m = p.matcher(input);
        while(m.find()) {
            List<String> splitThings = new ArrayList<>();
            splitThings.addAll(Arrays.asList(m.group(1).split(",")));
            for (int i=0;i<splitThings.size();i++) {
                String splitColumn = splitThings.get(i);
                CassandraColumn column = new CassandraColumn();
                column.setName(splitColumn);
                column.setType(splitColumn);
                column.setProperties(splitColumn);
                columns.add(column);
                if(column.properties!=null&&column.properties.equals("PRIMARY KEY"))
                    hasPrimaryKey = true;
            }
            if(!hasPrimaryKey){
                lookForPrimaryKey(input);
            }
        }
    }
}
