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
        String refVersion,compVersion;
        refVersion = "";
        compVersion = "";
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
            for (String refContent : refContents) {
                if (refContent.contains("CREATE TABLE")) {
                    CassandraTable refTable = new CassandraTable();
                    refTable.setName(refContent);
                    refTable.setColumns(refContent);
                    refTables.add(refTable);
                } else if (refContent.contains("CREATE INDEX") || refContent.contains("CREATE CUSTOM INDEX")) {
                    CassandraIndex refIndex = new CassandraIndex();
                    refIndex.setName(refContent);
                    refIndex.setTableName(refContent);
                    refIndex.setIdentifier(refContent);
                    refIndexes.add(refIndex);
                } else if (refContent.contains("CREATE MATERALIZED VIEW")) {
                    CassandraView refView = new CassandraView();
                    refView.setName(refContent);
                    refView.setStatement(refContent);
                    refView.setPrimaryKey(refContent);
                    refViews.add(refView);
                } else if (refContent.contains("CREATE TRIGGER")) {
                    CassandraTrigger refTrigger = new CassandraTrigger();
                    refTrigger.setName(refContent);
                    refTrigger.setTable(refContent);
                    refTrigger.setLogicFile(refContent);
                    refTriggers.add(refTrigger);
                } else if (refContent.contains("Database version")){
                    refVersion = refContent.substring(refContent.indexOf(": "), refContent.indexOf(";")).replace(':', ' ').trim();
                }
            }
            compTables = new ArrayList<>();
            compIndexes = new ArrayList<>();
            compViews = new ArrayList<>();
            compTriggers = new ArrayList<>();
            for (String compContent : compContents) {
                if (compContent.contains("CREATE TABLE")) {
                    CassandraTable compTable = new CassandraTable();
                    compTable.setName(compContent);
                    compTable.setColumns(compContent);
                    compTables.add(compTable);
                } else if (compContent.contains("CREATE INDEX") || compContent.contains("CREATE CUSTOM INDEX")) {
                    CassandraIndex compIndex = new CassandraIndex();
                    compIndex.setName(compContent);
                    compIndex.setTableName(compContent);
                    compIndex.setIdentifier(compContent);
                    compIndexes.add(compIndex);
                } else if (compContent.contains("CREATE MATERALIZED VIEW")) {
                    CassandraView compView = new CassandraView();
                    compView.setName(compContent);
                    compView.setStatement(compContent);
                    compView.setPrimaryKey(compContent);
                    compViews.add(compView);
                } else if (compContent.contains("CREATE TRIGGER")) {
                    CassandraTrigger compTrigger = new CassandraTrigger();
                    compTrigger.setName(compContent);
                    compTrigger.setTable(compContent);
                    compTrigger.setLogicFile(compContent);
                    compTriggers.add(compTrigger);
                } else if (compContent.contains("Database version")){
                    compVersion = compContent.substring(compContent.indexOf(": "), compContent.indexOf(";")).replace(':', ' ').trim();
                }
            }
            if(refVersion.equals(compVersion)){
                System.out.println("Obie bazy są w wersji "+refVersion);
            } else {
                System.out.println("Wersja bazy referenycjnej: "+refVersion);
                System.out.println("Wersja bazy porównywanej: "+compVersion);
            }
            compareTables(refTables,compTables);
            compareIndexes(refIndexes,compIndexes);
            compareViews(refViews,compViews);
            compareTriggers(refTriggers,compTriggers);
        } 
    }
    
    private static void compareColumns(CassandraTable ref,CassandraTable comp) {
        List<String> fine,missing,unexcepted,changed;
        fine = new ArrayList<>();
        changed = new ArrayList<>();
        for(int a=0;a<ref.columns.size();a++){
            for(int b=0;b<comp.columns.size();b++){
                if(ref.columns.get(a).name.equals(comp.columns.get(b).name)){
                    fine.add(comp.columns.get(b).name);
                    if(!(ref.columns.get(a).type.equals(comp.columns.get(b).type)))
                        changed.add(ref.name+"."+ref.columns.get(a).name+"\n\t\t typ zmieniony z "+ref.columns.get(a).type+" na "+comp.columns.get(b).type);
                    b=comp.columns.size();
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

    private static String prepareSnapshot(String source) {
        String prepared = source.replace("\n", "").replace(";", ";\n");
        return prepared;
    }

    private static void compareTables(List<CassandraTable> refTables, List<CassandraTable> compTables) {
        List<CassandraTable> fine, otherFine, missing, unexcepted;
        fine = new ArrayList<>();
        otherFine = new ArrayList<>();
        missing = new ArrayList<>();
        for(int r=0;r<refTables.size();r++){
            for(int c=0;c<compTables.size();c++){
                if(refTables.get(r).name.equals(compTables.get(c).name)){
                    otherFine.add(refTables.get(r));
                    fine.add(compTables.get(c));
                    c=compTables.size();
                } else if (c==compTables.size()-1) {
                    missing.add(refTables.get(r));
                }
            }
        }
        unexcepted = new ArrayList<>();
        for(int c=0;c<compTables.size();c++){
            for(int f=0;f<fine.size();f++){
                if(compTables.get(c).name.equals(fine.get(f).name)){
                    f=fine.size();
                } else if (f==fine.size()-1) {
                    unexcepted.add(compTables.get(c));
                }
            }
        }
        System.out.print("Brakujące tabele: ");
        if(missing.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int m=0;m<missing.size();m++)
                System.out.println("\n\t"+missing.get(m).name);
        }
        System.out.print("Niespodziewane tabele: ");
        if(unexcepted.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int u=0;u<unexcepted.size();u++)
                System.out.println("\n\t"+unexcepted.get(u).name);
        }
        for(int f=0;f<fine.size();f++)
            compareColumns(otherFine.get(f), fine.get(f));
    }

    private static void compareIndexes(List<CassandraIndex> refIndexes, List<CassandraIndex> compIndexes) {
        List<CassandraIndex> fine,otherFine,missing,unexcepted;
        fine = new ArrayList<>();
        otherFine = new ArrayList<>();
        missing = new ArrayList<>();
        for(int r=0;r<refIndexes.size();r++){
            for(int c=0;c<compIndexes.size();c++){
                if(refIndexes.get(r).name.equals(compIndexes.get(c).name)){
                    otherFine.add(refIndexes.get(r));
                    fine.add(compIndexes.get(c));
                    c=compIndexes.size();
                } else if(c==compIndexes.size()-1){
                    missing.add(refIndexes.get(r));
                }
            }
        }
        unexcepted = new ArrayList<>();
        for(int c=0;c<compIndexes.size();c++){
            for(int f=0;f<fine.size();f++){
                if(compIndexes.get(c).name.equals(fine.get(f).name)){
                    f=fine.size();
                } else if (f==fine.size()-1) {
                    unexcepted.add(compIndexes.get(c));
                }
            }
        }
        List<String> changed = new ArrayList<>();
        for(int f=0;f<fine.size();f++){
            if(!(otherFine.get(f).tableName.equals(fine.get(f).tableName))){
                changed.add(otherFine.get(f).name+": nazwa tabeli zmieniona z "+otherFine.get(f).tableName+
                        " na "+fine.get(f).tableName);
            }
            if(!(otherFine.get(f).identifier.equals(fine.get(f).identifier))){
                changed.add(otherFine.get(f).name+": identyfikator zmieniony z "+otherFine.get(f).identifier+
                        " na "+fine.get(f).identifier);
            }
        }
        System.out.print("Brakujące indeksy: ");
        if(missing.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int m=0;m<missing.size();m++)
                System.out.println("\n\t"+missing.get(m).name);
        }
        System.out.print("Niespodziewane indeksy: ");
        if(unexcepted.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int u=0;u<unexcepted.size();u++)
                System.out.println("\n\t"+unexcepted.get(u).name);
        }
        System.out.print("Zmiany w indeksach: ");
        if(changed.isEmpty()){
            System.out.println("BRAK");
        } else for (int c=0;c<changed.size();c++){
            System.out.println("\n\t"+changed.get(c));
        }
    }

    private static void compareViews(List<CassandraView> refViews, List<CassandraView> compViews) {
        List<CassandraView> fine,otherFine,missing,unexcepted;
        fine = new ArrayList<>();
        otherFine = new ArrayList<>();
        missing = new ArrayList<>();
        for(int r=0;r<refViews.size();r++){
            for(int c=0;c<compViews.size();c++){
                if(refViews.get(r).name.equals(compViews.get(c).name)){
                    otherFine.add(refViews.get(r));
                    fine.add(compViews.get(c));
                    c=compViews.size();
                } else if(c==compViews.size()-1){
                    missing.add(refViews.get(r));
                }
            }
        }
        unexcepted = new ArrayList<>();
        for(int c=0;c<compViews.size();c++){
            for(int f=0;f<fine.size();f++){
                if(compViews.get(c).name.equals(fine.get(f).name)){
                    f=fine.size();
                } else if (f==fine.size()-1) {
                    unexcepted.add(compViews.get(c));
                }
            }
        }
        List<String> changed = new ArrayList<>();
        for(int f=0;f<fine.size();f++){
            if(!(otherFine.get(f).primaryKey.equals(fine.get(f).primaryKey))){
                changed.add(otherFine.get(f).name+": klucz podstawowy zmieniony z "+otherFine.get(f).primaryKey+
                        " na "+fine.get(f).primaryKey);
            }
            if(!(otherFine.get(f).statement.equals(fine.get(f).statement))){
                changed.add(otherFine.get(f).name+": kwerenda zmieniona z "+otherFine.get(f).statement+
                        " na "+fine.get(f).statement);
            }
        }
        System.out.print("Brakujące perspektywy: ");
        if(missing.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int m=0;m<missing.size();m++)
                System.out.println("\n\t"+missing.get(m).name);
        }
        System.out.print("Niespodziewane perspektywy: ");
        if(unexcepted.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int u=0;u<unexcepted.size();u++)
                System.out.println("\n\t"+unexcepted.get(u).name);
        }
        System.out.print("Zmiany w perspektywach: ");
        if(changed.isEmpty()){
            System.out.println("BRAK");
        } else for (int c=0;c<changed.size();c++){
            System.out.print("\n\t"+changed.get(c));
        }
    }

    private static void compareTriggers(List<CassandraTrigger> refTriggers, List<CassandraTrigger> compTriggers) {
        List<CassandraTrigger> fine,otherFine,missing,unexcepted;
        fine = new ArrayList<>();
        otherFine = new ArrayList<>();
        missing = new ArrayList<>();
        for(int r=0;r<refTriggers.size();r++){
            for(int c=0;c<compTriggers.size();c++){
                if(refTriggers.get(r).name.equals(compTriggers.get(c).name)){
                    otherFine.add(refTriggers.get(r));
                    fine.add(compTriggers.get(c));
                    c=compTriggers.size();
                } else if(c==compTriggers.size()-1){
                    missing.add(refTriggers.get(r));
                }
            }
        }
        unexcepted = new ArrayList<>();
        for(int c=0;c<compTriggers.size();c++){
            for(int f=0;f<fine.size();f++){
                if(compTriggers.get(c).name.equals(fine.get(f).name)){
                    f=fine.size();
                } else if (f==fine.size()-1) {
                    unexcepted.add(compTriggers.get(c));
                }
            }
        }
        List<String> changed = new ArrayList<>();
        for(int f=0;f<fine.size();f++){
            if(!(otherFine.get(f).table.equals(fine.get(f).table))){
                changed.add(otherFine.get(f).name+": tabela zmieniona z "+otherFine.get(f).table+
                        " na "+fine.get(f).table);
            }
            if(!(otherFine.get(f).logicFile.equals(fine.get(f).logicFile))){
                changed.add(otherFine.get(f).name+": plik wyzwalacza z "+otherFine.get(f).logicFile+
                        " na "+fine.get(f).logicFile);
            }
        }
        System.out.print("Brakujące wyzwalacze: ");
        if(missing.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int m=0;m<missing.size();m++)
                System.out.println("\n\t"+missing.get(m).name);
        }
        System.out.print("Niespodziewane wyzwalacze: ");
        if(unexcepted.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int u=0;u<unexcepted.size();u++)
                System.out.println("\n\t"+unexcepted.get(u).name);
        }
        System.out.print("Zmiany w wyzwalaczach: ");
        if(changed.isEmpty()){
            System.out.println("BRAK");
        } else for (int c=0;c<changed.size();c++){
            System.out.print("\n\t"+changed.get(c));
        }
    }
}
