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
        Pattern p = Pattern.compile("\\([^()]*\\)");
        Matcher m = p.matcher(input);
        while(m.find()){
            Pattern q = Pattern.compile("\\((.*)\\)");
            Matcher n = q.matcher(m.group(0));
            while(n.find()){
                List<String> splitPKColumns = new ArrayList<>();
                splitPKColumns.addAll(Arrays.asList(n.group(1).split(",")));
                for(int j=0;j<splitPKColumns.size();j++){
                    String splitPKColumn = splitPKColumns.get(j);
                    for (int i=0;i<columns.size();i++){
                        if (splitPKColumn.trim().equals(columns.get(i).name)){
                            columns.get(i).properties = "PRIMARY KEY";
                            if (j==0)
                                columns.get(i).properties = columns.get(i).properties.concat(" PARTITION KEY");
                            else
                                columns.get(i).properties = columns.get(i).properties.concat(" CLUSTERING KEY");
                        }
                    } 
                }
            }
        }
    }

    private void checkForCollections(List<String> splitColumns) {
        List<Integer> elementsToDelete = new ArrayList<>();
        for(int i=0;i<splitColumns.size();i++){
            if(splitColumns.get(i).contains("<")){
                int j=i;
                do {
                    j++;
                    splitColumns.set(i, splitColumns.get(i).concat(","+splitColumns.get(j)));
                    elementsToDelete.add(j);
                } while (!splitColumns.get(j).contains(">"));
            }
        }
        for(int d=elementsToDelete.size()-1;d>=0;d--){
            splitColumns.remove(splitColumns.get(elementsToDelete.get(d)));
        }
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
            input = input.substring(input.indexOf(type)+type.length()).trim();
            if(input.toUpperCase().equals("PRIMARY KEY"))
                properties = input.toUpperCase();
            else if (input.contains("<"))
                type = type.concat(input);
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
        Pattern p = Pattern.compile("\\((.*)\\)");
        Matcher m = p.matcher(input);
        while(m.find()) {
            List<String> splitColumns = new ArrayList<>();
            splitColumns.addAll(Arrays.asList(m.group(1).split(",")));
            checkForCollections(splitColumns);
            for (int i=0;i<splitColumns.size();i++) {
                String splitColumn = splitColumns.get(i);
                if(!splitColumn.startsWith("PRIMARY KEY")&&!splitColumn.contains(")")){
                    CassandraColumn column = new CassandraColumn();
                    column.setName(splitColumn);
                    column.setType(splitColumn);
                    column.setProperties(splitColumn);
                    columns.add(column);
                if(column.properties!=null&&column.properties.contains("PRIMARY KEY"))
                    hasPrimaryKey = true;
                }
            }
            if(!hasPrimaryKey){
                lookForPrimaryKey(input);
            }
        }
    }
}
