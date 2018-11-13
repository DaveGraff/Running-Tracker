/*
 * David Graff 2018
 */
package running.tracker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *  This class takes advantage of shallow copies 
 *  to neatly save user data
 * @author david
 */
public final class SaveFile {
    private final String fileName;
    private ArrayList<Distance> toSave;
    
    public SaveFile(String fileName){
        this.fileName = fileName;
        toSave = loadFromFile();
    }
    
    /**
     * Loads saved data from the given file
     * @return The saved data, or an empty list if none
     * is found
     */
    public ArrayList<Distance> loadFromFile() {
        ArrayList<Distance> temp;
        try {
            FileInputStream fis = new FileInputStream(new File(fileName));
            ObjectInputStream reader = new ObjectInputStream(fis);

            temp = (ArrayList<Distance>) reader.readObject();

            reader.close();
            fis.close();
        } catch (IOException | ClassNotFoundException e) {
            temp = new ArrayList<>();
        }
        toSave = temp;
        return toSave;
    }
    
    /**
     * Saves data to given file
     */
    public void saveToFile(){
        try{
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            ObjectOutputStream writer = new ObjectOutputStream(fos);
            
            writer.writeObject(toSave);
            
            writer.close();
            fos.close();
        } catch(IOException e){}//Nothing to do
    }
}
