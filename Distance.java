/*
 * David Graff 2018
 */
package running.tracker.pkg2.pkg0;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author david
 */
public class Distance implements Serializable{
    private String name;
    private final ArrayList<Run> runs;
    private Sorting sort = Sorting.unsorted;
            
    public Distance(String name){
        this.name = name;
        runs = new ArrayList<>();
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the runs
     */
    public ArrayList<Run> getRuns() {
        return runs;
    }
    
    /**
     * Sorts times for a race fastest to slowest
     * @return whether or not sorting was needed
     */
    public boolean sortByTimeFastest(){
        if(sort != Sorting.fastest){
            Collections.sort(runs, new sortByTime(true));
            sort = Sorting.fastest;
            return true;
        }
        return false;
    }
    
    /**
     * Sorts times for a race slowest to fastest
     * @return whether or not sorting was needed
     */
    public boolean sortByTimeSlowest(){
        if(sort != Sorting.slowest){
            Collections.sort(runs, new sortByTime(false));
            sort = Sorting.slowest;
            return true;
        }
        return false;
    }
    
    /**
     * Base helper class for sorting times
     */
    class sortByTime implements Comparator<Run> { 
        private final boolean ascending;
        public sortByTime(boolean ascending){
            this.ascending = ascending;
        }
        @Override
        public int compare(Run a, Run b){
            int compare = 0;
            if(a.getMinutes() > b.getMinutes())
                compare =  1;
            else if(a.getMinutes() < b.getMinutes())
                compare = -1;
            else if(a.getMinutes() == b.getMinutes()){
                if(a.getSeconds() > b.getSeconds())
                    compare = 1;
                if(a.getSeconds() < b.getSeconds())
                    compare = -1;
            }
            else compare = 0;
            if(!ascending)
                return compare *-1;
            return compare;
        } 
    }
    
    /**
     * Sorts times for a race newest to oldest
     * @return whether or not sorting was needed
     */
    public boolean sortByDateNewest(){
        if(sort != Sorting.newest){
            Collections.sort(runs, new sortByDate(true));
            sort = Sorting.newest;
            return true;
        }
        return false;
    }
    
    /**
     * Sort times for a race oldest to newest
     * @return whether or not sorting was needed
     */
    public boolean sortByDateOldest(){
        if(sort != Sorting.oldest){
            Collections.sort(runs, new sortByDate(false));
            sort = Sorting.oldest;
            return true;
        }
        return false;
    }
    
    /**
     * Base helper class for sorting date objects
     */
    class sortByDate implements Comparator<Run>{
        private final boolean ascending;
        public sortByDate(boolean ascending){
            this.ascending = ascending;
        }
        @Override
        public int compare(Run a, Run b) {
            int compare = a.getDate().compareTo(b.getDate());
            if(!ascending)
                return compare * -1;
            return compare;
        }
        
    }
    
    /**
     * 
     * @param distances object to be saved
     * @param save Save object
     */
    public void viewDistance(ArrayList<Distance> distances, SaveFile save){
        Stage editStage = new Stage();
        editStage.setTitle(name);
                        
        ScrollPane scroller = new ScrollPane();
        scroller.setMinSize(345, 250);
        scroller.setMaxSize(345, 250);
        viewDistanceRender(scroller, distances, save);
        
        Button sortTime = new Button("Sort by time");
        sortTime.setOnAction(e -> {
            boolean isRun;
            if(sort != Sorting.slowest)
               isRun =  sortByTimeSlowest();
            else
               isRun = sortByTimeFastest();
            if(isRun){
                viewDistanceRender(scroller, distances, save);
                save.saveToFile(distances);
            }
        });
        
        Button sortDate = new Button("Sort by date");
        sortDate.setOnAction(e -> {
            boolean isRun;
            if(sort != Sorting.newest)
               isRun =  sortByDateNewest();
            else
               isRun = sortByDateOldest();
            if(isRun){
                viewDistanceRender(scroller, distances, save);
                save.saveToFile(distances);
            }
        });
        
        Button addRun = new Button("Add Run");addRun.setDefaultButton(true);
        addRun.setOnAction(e -> addRun(distances, save, scroller));        
        Button close = new Button("Close");close.setCancelButton(true);
        close.setOnAction(e -> editStage.close());
        
        VBox pane = new VBox(new ToolBar(sortTime, sortDate), scroller, new HBox(addRun, close));
        Scene scene = new Scene(pane);
        scene.getStylesheets().add(this.getClass().getResource("RunningStyle.css").toExternalForm());
        editStage.setScene(scene);
        editStage.showAndWait();
    }
    
    /**
     * Inner method to render the list of runs for a set
     * distance
     * @param scroller
     * @param distances
     * @param save 
     */
    private void viewDistanceRender(ScrollPane scroller, ArrayList<Distance> distances, SaveFile save){
        VBox runPane = new VBox();
        runs.forEach(run ->{
            Label date = new Label(run.getDate() + "\t");
            Label time = new Label(run.getTime() + "\t");
            Button edit = new Button("Edit");
            edit.setOnAction(e -> {
                run.editRun(distances, save);
                viewDistanceRender(scroller, distances, save);
            });
            Button remove = new Button("Remove");
            remove.setOnAction(e -> {
                runs.remove(run);
                viewDistanceRender(scroller, distances, save);
                save.saveToFile(distances);
            });
            runPane.getChildren().add(new HBox(date, time, edit, remove));
        });
        scroller.setContent(runPane);
    }
    
    private void addRun(ArrayList<Distance> distances, SaveFile saveFile, ScrollPane scroller){
        Run temp = new Run(0, 0, new Date());
        temp.editRun(distances, saveFile);
        if(temp.getMinutes() > 0 || temp.getSeconds() > 0){
            runs.add(temp);
            saveFile.saveToFile(distances);
            viewDistanceRender(scroller, distances, saveFile);
        }
    }
    
}
