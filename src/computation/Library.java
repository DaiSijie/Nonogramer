/*
 *	Author:      Gilbert Maystre
 *	Date:        Aug 31, 2014
 */

package computation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

//COMMENT : all grids are differcianted by their name, therefore they should be unique and thus controlled when added to library

public final class Library {

    private static Library instanceOf;

    private final HashMap<String, ArrayList<String>> rawGrids;
    private final HashMap<Integer, ArrayList<String>> gridsByLevel;

    private final static String LIBRARY_NAME = "library.txt";
    private File libraryFile;

    private Library(){
        rawGrids = new HashMap<>();
        gridsByLevel = new HashMap<>();

        try {
            libraryFile = checkFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }        

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(libraryFile));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        String line;

        try {
            while((line = reader.readLine()) != null){
                String[] parsed = line.split(";");
                String name = parsed[0];
                Integer level = new Integer(parsed[1]);
                String stream = parsed[2];

                ArrayList<String> toPut = new ArrayList<>(2);
                toPut.add(level.toString());
                toPut.add(stream);

                //on met les infos de la grille dans la map des noms
                rawGrids.put(name, toPut);

                //on met le nom dans la map des niveaux
                if(!gridsByLevel.containsKey(level))
                    gridsByLevel.put(level, new ArrayList<String>());

                gridsByLevel.get(level).add(name);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File checkFile() throws IOException{
        String userHome = System.getProperty("user.home");
        String fileSeparator = System.getProperty("file.separator");

        File folder = new File(userHome+fileSeparator+".nonogramer");
        if(!folder.exists())
            folder.mkdir();

        File libraryFile = new File(folder.getAbsolutePath()+fileSeparator+"."+LIBRARY_NAME);
        if(!libraryFile.exists()){

            InputStream is = getClass().getResourceAsStream("/"+LIBRARY_NAME); 
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(libraryFile, true)));

            String line;
            while((line = reader.readLine()) != null)
                out.println(line);

            out.close();
            reader.close();
        }
        return libraryFile;
    }

    public static Library getInstanceOf(){

        if(instanceOf == null)
            instanceOf = new Library();
        return instanceOf;

    }

    public ArrayList<String> getGridsNameForLevel(int level){

        return gridsByLevel.get(level);

    }

    public String getRawDataFor(String name){
        return rawGrids.get(name).get(1);
    }
    
    public NonogramGrid getGridForName(String name){

        ArrayList<String> infos = rawGrids.get(name);

        int level = new Integer(infos.get(0));
        String rawFlow = infos.get(1);

        NonogramGrid toReturn = Parsers.rawFlowToNonogramGrid(rawFlow, name);
        toReturn.setLevel(level);

        return toReturn;
    }

    public boolean nameOk(String name){

        return !name.isEmpty() && !rawGrids.keySet().contains(name) && !name.contains(";");

    }

    public void addGridToLibrary(String name, int level, String rawFlow) throws IllegalArgumentException{

        if(!nameOk(name))
            throw new IllegalArgumentException("Name is already present");

        //on l'écrit dans le fichier
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(libraryFile, true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println(name+";"+level+";"+rawFlow);
        out.close();

        //on met a jour les maps
        ArrayList<String> toPut = new ArrayList<>(3);
        toPut.add(""+level);
        toPut.add(rawFlow);

        rawGrids.put(name, toPut);

        if(!gridsByLevel.containsKey(level))
            gridsByLevel.put(level, new ArrayList<String>());

        gridsByLevel.get(level).add(name);
    }

    public void deleteGrid(String name){
        if(!rawGrids.containsKey(name))
            throw new IllegalArgumentException("The grid does not exists");

        gridsByLevel.get(new Integer(rawGrids.get(name).get(0))).remove(name);
        rawGrids.remove(name);

        rewriteEverything();
    }

    public void restore(){
        libraryFile.delete();
        
        instanceOf = new Library();
    }
    
    private void rewriteEverything(){
        libraryFile.delete();

        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(libraryFile, true)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        for(Entry<String, ArrayList<String>> entry : rawGrids.entrySet()){
            Integer level = new Integer(entry.getValue().get(0));
            String rawFlow = entry.getValue().get(1);
            
            out.println(entry.getKey()+";"+level+";"+rawFlow);
        }
        
        out.close();
    }

}
