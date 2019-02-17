/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cassandraanalyser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author User
 */
public class CassandraAnalyser {

    private static final List<String> ALTERS = new ArrayList<>();
    /**
     * @param args the command line arguments
     */
      
    public static void main(String[] args) {
        String refVersion,compVersion;
        refVersion = "";
        compVersion = "";
        if(args==null||args.length<2){
            System.out.println("Zbyt mała ilość argumentów");
        } else if(args.length==3&&!(args[2].equals("-d")||args[2].equals("--diff"))) { 
            System.out.println("Niepoprawny argument "+args[2]);
        } else {
            boolean passiveMode = false;
            if(args.length==3&&(args[2].equals("-d"))){
                passiveMode = true;
            }
            String refUnprepared = "";
            String compUnprepared = "";
            try {
                List<String> refFile = Files.readAllLines(Paths.get(args[0]));
                for(int a=0;a<refFile.size();a++){
                    refUnprepared = refUnprepared.concat(refFile.get(a)+"\n");
                }
                List<String> compFile = Files.readAllLines(Paths.get(args[1]));
                for(int a=0;a<compFile.size();a++){
                    compUnprepared = compUnprepared.concat(compFile.get(a)+"\n");
                }
            } catch (Exception e) {
                System.out.println(e.getLocalizedMessage());
            }
            String ref = prepareSnapshot(refUnprepared);
            String comp = prepareSnapshot(compUnprepared);
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
            Pattern tablePattern = Pattern.compile("\\bCREATE\\b\\s+\\bTABLE\\b",Pattern.CASE_INSENSITIVE);
            Pattern indexPattern = Pattern.compile("\\bCREATE\\b\\s+\\bINDEX\\b|"
                    + "\\bCREATE\\b\\s+\\bCUSTOM\\b\\s+\\bINDEX\\b",Pattern.CASE_INSENSITIVE);
            Pattern viewPattern = Pattern.compile("\\bCREATE\\b\\s+\\bMATERIALIZED\\b\\s+\\bVIEW\\b",Pattern.CASE_INSENSITIVE);
            Pattern triggerPattern = Pattern.compile("\\bCREATE\\b\\s+\\bTRIGGER\\b",Pattern.CASE_INSENSITIVE);
            Pattern versionPattern = Pattern.compile("database\\b\\s+\\bversion",Pattern.CASE_INSENSITIVE);
            Matcher tableMatcher,indexMatcher,viewMatcher,triggerMatcher,versionMatcher;
            for (String refContent : refContents) {
                tableMatcher = tablePattern.matcher(refContent);
                indexMatcher = indexPattern.matcher(refContent);
                viewMatcher = viewPattern.matcher(refContent);
                triggerMatcher = triggerPattern.matcher(refContent);
                versionMatcher = versionPattern.matcher(refContent);
                if (tableMatcher.lookingAt()) {
                    CassandraTable refTable = new CassandraTable();
                    refTable.setName(refContent);
                    refTable.setColumns(refContent);
                    refTables.add(refTable);
                } else if (indexMatcher.lookingAt()) {
                    CassandraIndex refIndex = new CassandraIndex();
                    refIndex.setName(refContent);
                    refIndex.setTableName(refContent);
                    refIndex.setIdentifier(refContent);
                    if(refIndex.lookForIndexingLib)
                        refIndex.setIndexingLib(refContent);
                    refIndexes.add(refIndex);
                } else if (viewMatcher.lookingAt()) {
                    CassandraView refView = new CassandraView();
                    refView.setName(refContent);
                    refView.setStatement(refContent);
                    refView.setPrimaryKey(refContent);
                    refViews.add(refView);
                } else if (triggerMatcher.lookingAt()) {
                    CassandraTrigger refTrigger = new CassandraTrigger();
                    refTrigger.setName(refContent);
                    refTrigger.setTable(refContent);
                    refTrigger.setLogicFile(refContent);
                    refTriggers.add(refTrigger);
                } else if (versionMatcher.find()){
                    refVersion = refContent.substring(refContent.indexOf(": "), refContent.indexOf(";")).replace(':', ' ').trim();
                }
            }
            compTables = new ArrayList<>();
            compIndexes = new ArrayList<>();
            compViews = new ArrayList<>();
            compTriggers = new ArrayList<>();
            for (String compContent : compContents) {
                tableMatcher = tablePattern.matcher(compContent);
                indexMatcher = indexPattern.matcher(compContent);
                viewMatcher = viewPattern.matcher(compContent);
                triggerMatcher = triggerPattern.matcher(compContent);
                versionMatcher = versionPattern.matcher(compContent);
                if (tableMatcher.lookingAt()) {
                    CassandraTable compTable = new CassandraTable();
                    compTable.setName(compContent);
                    compTable.setColumns(compContent);
                    compTables.add(compTable);
                } else if (indexMatcher.lookingAt()) {
                    CassandraIndex compIndex = new CassandraIndex();
                    compIndex.setName(compContent);
                    compIndex.setTableName(compContent);
                    compIndex.setIdentifier(compContent);
                    if(compIndex.lookForIndexingLib)
                        compIndex.setIndexingLib(compContent);
                    compIndexes.add(compIndex);
                } else if (viewMatcher.lookingAt()) {
                    CassandraView compView = new CassandraView();
                    compView.setName(compContent);
                    compView.setStatement(compContent);
                    compView.setPrimaryKey(compContent);
                    compViews.add(compView);
                } else if (triggerMatcher.lookingAt()) {
                    CassandraTrigger compTrigger = new CassandraTrigger();
                    compTrigger.setName(compContent);
                    compTrigger.setTable(compContent);
                    compTrigger.setLogicFile(compContent);
                    compTriggers.add(compTrigger);
                } else if (versionMatcher.find()){
                    compVersion = compContent.substring(compContent.indexOf(":"), compContent.indexOf(";")).replace(':', ' ').trim();
                }
            }
            if(refVersion.equals(compVersion)&&!(refVersion.equals(""))){
                System.out.println("Obie bazy są w wersji "+refVersion);
            } else {
                System.out.println("Wersja bazy referenycjnej: "+refVersion);
                System.out.println("Wersja bazy porównywanej: "+compVersion);
            }
            compareTables(refTables,compTables);
            compareIndexes(refIndexes,compIndexes);
            compareViews(refViews,compViews);
            compareTriggers(refTriggers,compTriggers);
            if(!passiveMode){
                List<String> altersToSave = new ArrayList<>();
                altersToSave.add("--alters to reference database;");
                for(int a=0;a<ALTERS.size();a++){
                    altersToSave.add(ALTERS.get(a));
                }
                if(!ALTERS.isEmpty()){
                    BufferedWriter writer = null;
                    try {
                        String saveFileName = "alters.cql";
                        File saveFile = new File(saveFileName);
                        System.out.println("Zapisywanie zmian do pliku "+saveFile.getCanonicalPath());
                        writer = new BufferedWriter(new FileWriter(saveFile));

                        for(int i=0;i<altersToSave.size();i++){
                            System.out.println(altersToSave.get(i));
                            writer.write(altersToSave.get(i)+"\n");
                        }
                    } catch (Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    } finally {
                        try {
                            writer.close();
                        } catch (Exception e) {
                            System.out.println(e.getLocalizedMessage());
                        }
                    }
                } else System.out.println("Brak możliwości wygenerowania ALTER TABLE :(");
            }
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
                    if(!(ref.columns.get(a).type.equals(comp.columns.get(b).type))){
                        changed.add(ref.name+"."+ref.columns.get(a).name+"\n\t\t typ zmieniony z "+ref.columns.get(a).type+" na "+comp.columns.get(b).type);
                        ALTERS.add("ALTER TABLE "+ref.name+" ALTER "
                                +ref.columns.get(a).name+" TYPE "+comp.columns.get(b).type+";");
                    }
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
                    ALTERS.add("ALTER TABLE "+ref.name+" DROP "
                                +ref.columns.get(a).name+";");
                }
            }
        }
        unexcepted = new ArrayList<>();
        for(int b=0;b<comp.columns.size();b++){
            if(fine.isEmpty()){
                unexcepted.add(comp.name+"."+comp.columns.get(b).name);
                ALTERS.add("ALTER TABLE "+ref.name+" ADD ("
                            +comp.columns.get(b).name+" "+comp.columns.get(b).type+");");
            }
            for(int c=0;c<fine.size();c++){
                if(fine.get(c).equals(comp.columns.get(b).name)){
                    c=fine.size();
                } else if (c==fine.size()-1){
                    unexcepted.add(comp.name+"."+comp.columns.get(b).name);
                    ALTERS.add("ALTER TABLE "+ref.name+" ADD ("
                                +comp.columns.get(b).name+" "+comp.columns.get(b).type+");");
                }
            }
        }
        System.out.print("Zmienione kolumny w tabeli \""+comp.name+"\" : ");
        if(changed.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int h=0;h<changed.size();h++)
                System.out.println("\n\t"+changed.get(h));
        }
        System.out.print("Brakujące kolumny w tabeli \""+comp.name+"\" : ");
        if(missing.isEmpty()){
            System.out.println("BRAK");
        } else {
            for(int m=0;m<missing.size();m++)
                System.out.println("\n\t"+missing.get(m));
        }
        System.out.print("Niespodziewane kolumny w tabeli \""+comp.name+"\" : ");
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
                    //alters.add(); czy dodać jeszcze instrukcje zrzucania tabeli?
                }
            }
        }
        unexcepted = new ArrayList<>();
        for(int c=0;c<compTables.size();c++){
            if(fine.isEmpty())
                unexcepted.add(compTables.get(c));
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
            if(fine.isEmpty())
                unexcepted.add(compIndexes.get(c));
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
            if(fine.get(f).indexingLib!=null&&otherFine.get(f).indexingLib!=null){
                if(!(otherFine.get(f).indexingLib.equals(fine.get(f).indexingLib))){
                changed.add(otherFine.get(f).name+": bibl. indeksująca zmieniona z "+otherFine.get(f).indexingLib+
                        " na "+fine.get(f).indexingLib);
                }
            } else if (fine.get(f).indexingLib==null&&otherFine.get(f).indexingLib!=null) {
                changed.add(otherFine.get(f).name+": brak bibl. indeksującej "+otherFine.get(f).indexingLib);
            } else if (fine.get(f).indexingLib!=null&&otherFine.get(f).indexingLib==null) {
                changed.add(otherFine.get(f).name+": użyto bibl. indeksującej "+fine.get(f).indexingLib);
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
            if(fine.isEmpty())
                unexcepted.add(compViews.get(c));
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
            if(fine.isEmpty())
                unexcepted.add(compTriggers.get(c));
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
