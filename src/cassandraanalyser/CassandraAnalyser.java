/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
public class CassandraAnalyser {

    /**
     * @param args the command line arguments
     */
      
    public static void main(String[] args) {
        /*if(args[2]!=null&&(args[2].equals("--diff")||args[2].equals("-d"))){
            System.out.println("Do this");
        } else {
            System.out.println("Do that");
        }
        String ref = "CREATE TABLE przyklad1 (" +
            "moviename text PRMIARYKEY," +
            "rentID int," +
            "smh int"+
            ");";
        String comp = "CREATE TABLE przyklad2 (" +
            "moviename text PRMIARYKEY," +
            "rentID text," +
            "expDate text" +
            ");";*/
        String ref = "";
        String comp = "";
        if(ref.contains("CREATE TABLE")&&comp.contains("CREATE TABLE")){
            CassandraTable refTable = new CassandraTable();
            CassandraTable compTable = new CassandraTable();
            refTable.setName(ref);
            refTable.setColumns(ref);
            compTable.setName(comp);
            compTable.setColumns(comp);
            compareTables(refTable, compTable);
        } else if ((ref.contains("CREATE INDEX")||
                ref.contains("CREATE CUSTOM INDEX"))&&
                (comp.contains("CREATE INDEX")||
                comp.contains("CREATE CUSTOM INDEX"))) {
            CassandraIndex refIndex = new CassandraIndex();
            CassandraIndex compIndex = new CassandraIndex();
            refIndex.setName(ref);
            refIndex.setTableName(ref);
            refIndex.setIdentifier(ref);
            compIndex.setName(comp);
            compIndex.setTableName(comp);
            compIndex.setIdentifier(comp);
            compareIndexes(refIndex, compIndex);
        } else {
            System.out.println("Do that");
        }
    }
    
    private static void compareTables(CassandraTable ref,CassandraTable comp) {
        List<String> fine,missing,unexcepted,changed;
        fine = new ArrayList<>();
        changed = new ArrayList<>();
        for(int a=0;a<ref.columns.size();a++){
            for(int b=a;b<comp.columns.size();b++){
                if(ref.columns.get(a).name.equals(comp.columns.get(b).name)){
                    fine.add(ref.columns.get(a).name);
                    if(!(ref.columns.get(a).type.equals(comp.columns.get(b).type)))
                        changed.add(ref.name+"."+ref.columns.get(a).name+"\n\t\t typ zmieniony z "+ref.columns.get(a).type+" na "+comp.columns.get(b).type);
                }
            }
        }
        missing = new ArrayList<>();
        for(int a=0;a<ref.columns.size();a++){
            for(int c=0;c<fine.size();c++){
                if(fine.get(c).equals(ref.columns.get(a).name)){
                    c=fine.size();
                } else if (c==fine.size()-1){
                    missing.add(ref.name+"."+ref.columns.get(a).name);
                }
            }
        }
        unexcepted = new ArrayList<>();
        for(int b=0;b<comp.columns.size();b++){
            for(int c=0;c<fine.size();c++){
                if(fine.get(c).equals(comp.columns.get(b).name)){
                    c=fine.size();
                } else if (c==fine.size()-1){
                    unexcepted.add(comp.name+"."+comp.columns.get(b).name);
                }
            }
        }
        System.out.print("Zmienione kolumny: ");
        if(changed.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int h=0;h<changed.size();h++)
                System.out.println("\n\t"+changed.get(h));
        }
        System.out.print("Brakujące kolumny: ");
        if(missing.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int m=0;m<missing.size();m++)
                System.out.println("\n\t"+missing.get(m));
        }
        System.out.print("Niespodziewane kolumny: ");
        if(unexcepted.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int u=0;u<unexcepted.size();u++)
                System.out.println("\n\t"+unexcepted.get(u));
        }
    }

    private static void compareIndexes(CassandraIndex ref, CassandraIndex comp) {
        List<String> changed = new ArrayList<>();
        if(!(ref.tableName.equals(comp.tableName))){
            changed.add(ref.name+": nazwa tabeli zmieniona z "+ref.tableName+
                    " na "+comp.tableName);
        }
        if(!(ref.identifier.equals(comp.identifier))){
            changed.add(ref.name+": identyfikator zmieniony z "+ref.identifier+
                    " na "+comp.identifier);
        }
        System.out.print("Zmiany w indeksach: ");
        if(changed.isEmpty()){
            System.out.println("BRAK");
        } else for (int c=0;c<changed.size();c++){
            System.out.print("\n\t"+changed.get(c));
        }
    }
}
