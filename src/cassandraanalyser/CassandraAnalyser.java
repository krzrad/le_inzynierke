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
        if(args==null||args.length<2){
            System.out.println("Zbyt mała liość argumentów");
        } else {
            String ref = prepareSnapshot(args[0]);
            String comp = prepareSnapshot(args[1]);
            String[] refContents = ref.split("\n");
            String[] compContents = comp.split("\n");
            List<CassandraTable> refTables,compTables;
            List<CassandraIndex> refIndexes,compIndexes;
            List<CassandraView> refViews,compViews;
            List<CassandraTrigger> refTriggers,compTriggers;
            refTables = new ArrayList<>();
            refIndexes = new ArrayList<>();
            refViews = new ArrayList<>();
            refTriggers = new ArrayList<>();
            for (int r=0;r<refContents.length;r++){
                if(refContents[r].contains("CREATE TABLE")){
                    CassandraTable refTable = new CassandraTable();
                    refTable.setName(refContents[r]);
                    refTable.setColumns(refContents[r]);
                    refTables.add(refTable);
                } else if ((refContents[r].contains("CREATE INDEX")||
                            refContents[r].contains("CREATE CUSTOM INDEX"))){
                    CassandraIndex refIndex = new CassandraIndex();
                    refIndex.setName(refContents[r]);
                    refIndex.setTableName(refContents[r]);
                    refIndex.setIdentifier(refContents[r]);
                    refIndexes.add(refIndex);
                } else if (refContents[r].contains("CREATE MATERALIZED VIEW")){
                    CassandraView refView = new CassandraView();
                    refView.setName(refContents[r]);
                    refView.setStatement(refContents[r]);
                    refView.setPrimaryKey(refContents[r]);
                    refViews.add(refView);
                } else if (refContents[r].contains("CREATE TRIGGER")){
                    CassandraTrigger refTrigger = new CassandraTrigger();
                    refTrigger.setName(refContents[r]);
                    refTrigger.setTable(refContents[r]);
                    refTrigger.setLogicFile(refContents[r]);
                    refTriggers.add(refTrigger);
                }
            }
            compTables = new ArrayList<>();
            compIndexes = new ArrayList<>();
            compViews = new ArrayList<>();
            compTriggers = new ArrayList<>();
            for (int c=0;c<compContents.length;c++){
                if(compContents[c].contains("CREATE TABLE")){
                    CassandraTable compTable = new CassandraTable();
                    compTable.setName(compContents[c]);
                    compTable.setColumns(compContents[c]);
                    compTables.add(compTable);
                } else if ((compContents[c].contains("CREATE INDEX")||
                            compContents[c].contains("CREATE CUSTOM INDEX"))){
                    CassandraIndex compIndex = new CassandraIndex();
                    compIndex.setName(compContents[c]);
                    compIndex.setTableName(compContents[c]);
                    compIndex.setIdentifier(compContents[c]);
                    compIndexes.add(compIndex);
                } else if (compContents[c].contains("CREATE MATERALIZED VIEW")){
                    CassandraView compView = new CassandraView();
                    compView.setName(compContents[c]);
                    compView.setStatement(compContents[c]);
                    compView.setPrimaryKey(compContents[c]);
                    compViews.add(compView);
                } else if (compContents[c].contains("CREATE TRIGGER")){
                    CassandraTrigger compTrigger = new CassandraTrigger();
                    compTrigger.setName(compContents[c]);
                    compTrigger.setTable(compContents[c]);
                    compTrigger.setLogicFile(compContents[c]);
                    compTriggers.add(compTrigger);
                }
            }
            /*for(int r=0;r<refContents.length;r++){
                for(int c=0;c<compContents.length;c++){
                    if(refContents[r].contains("CREATE TABLE")&&compContents[c].contains("CREATE TABLE")){
                        CassandraTable refTable = new CassandraTable();
                        CassandraTable compTable = new CassandraTable();
                        refTable.setName(refContents[r]);
                        refTable.setColumns(refContents[r]);
                        compTable.setName(compContents[c]);
                        compTable.setColumns(compContents[c]);
                        compareTables(refTable, compTable);
                    } else if ((refContents[r].contains("CREATE INDEX")||
                            refContents[r].contains("CREATE CUSTOM INDEX"))&&
                            (compContents[c].contains("CREATE INDEX")||
                            compContents[c].contains("CREATE CUSTOM INDEX"))) {
                        CassandraIndex refIndex = new CassandraIndex();
                        CassandraIndex compIndex = new CassandraIndex();
                        refIndex.setName(refContents[r]);
                        refIndex.setTableName(refContents[r]);
                        refIndex.setIdentifier(refContents[r]);
                        compIndex.setName(compContents[c]);
                        compIndex.setTableName(compContents[c]);
                        compIndex.setIdentifier(compContents[c]);
                        compareIndexes(refIndex, compIndex);
                    } else {
                        System.out.println("Do that");
                    }
                }
            }*/
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

    private static String prepareSnapshot(String source) {
        String prepared = source.replace("\n", "").replace(";", ";\n");
        System.out.println(prepared);
        return prepared;
    }
}
