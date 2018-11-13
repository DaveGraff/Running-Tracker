/*
 * David Graff 2018
 */
package running.tracker.pkg2.pkg0;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 *
 * @author david
 */
public class SaveFile {
    private final String fileName;
    public SaveFile(String fileName){
        this.fileName = fileName;
    }
    
    /**
     * Loads saved data from the given file
     * @return The saved data, or an empty list if none
     * is found
     */
    public ArrayList<Distance> loadFromFile(){
        ArrayList<Distance> temp;
        try{
            FileInputStream fis = new FileInputStream(new File(fileName));
            ObjectInputStream reader = new ObjectInputStream(fis);
            
            temp = (ArrayList<Distance>) reader.readObject();
            
            reader.close();
            fis.close();
        } catch(IOException | ClassNotFoundException e){
            temp = new ArrayList<>();
        }
        return temp;
    }
    
    /**
     * Saves data to given file
     * @param input 
     */
    public void saveToFile(ArrayList<Distance> input){
        try{
            FileOutputStream fos = new FileOutputStream(new File(fileName));
            ObjectOutputStream writer = new ObjectOutputStream(fos);
            
            writer.writeObject(input);
            
            writer.close();
            fos.close();
        } catch(IOException e){}//Nothing to do
    }
}
