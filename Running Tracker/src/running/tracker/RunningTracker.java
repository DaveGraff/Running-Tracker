/*
 * David Graff 2018
 */
package running.tracker;

import java.util.ArrayList;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author david
 */
public class RunningTracker extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        SaveFile save = new SaveFile("distances.txt");
        ArrayList<Distance> distances = save.loadFromFile();
        ScrollPane pane = new ScrollPane();
        render(distances, pane, save);
        
        Button addDistance = new Button("Add Distance");addDistance.setDefaultButton(true);
        addDistance.setOnAction(e -> addDistance(distances, pane, save));
        Button close = new Button("Close");close.setCancelButton(true);
        close.setOnAction(e -> primaryStage.close());
        ToolBar toolbar = new ToolBar(addDistance, close);
                
        VBox main = new VBox(toolbar, pane);
        main.setMinSize(310, 400);
        Scene scene = new Scene(main);
        scene.getStylesheets().add(this.getClass().getResource("RunningStyle.css").toExternalForm());
        primaryStage.setTitle("Run Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    public void addDistance(ArrayList<Distance> distances, ScrollPane pane, SaveFile save){
        Stage newStage = new Stage();
        TextField enterName = new TextField("Enter Name");
        Button enter = new Button("Enter");enter.setDefaultButton(true);
        enter.setOnAction(e ->{
            if(!enterName.getText().equals("")){
                Distance temp = new Distance(enterName.getText());
                distances.add(temp);
                render(distances, pane, save);
                save.saveToFile(distances);
                newStage.close();
                temp.viewDistance(distances, save);
            }
        });
        Button cancel = new Button("Cancel");cancel.setCancelButton(true);
        cancel.setOnAction(e -> newStage.close());
        
        VBox temp = new VBox(enterName, new HBox(enter, cancel));
        Scene scene = new Scene(temp);
        scene.getStylesheets().add(this.getClass().getResource("RunningStyle.css").toExternalForm());
        newStage.setScene(scene);
        newStage.showAndWait();
    }
    
    /**
     * Private helper method to move an objects index
     * @param distances list of objects to check
     * @param moved Object to move
     * @param cmd 0 or 1, up or down
     * @return 
     */
    private boolean move(ArrayList<Distance> distances, Distance moved, int cmd){
        int index = distances.indexOf(moved);
        if(cmd == 0){//up
            if(index == 0)
                return false;
            distances.remove(moved);
            distances.add(index-1, moved);
        } else {//down
            if(index == distances.size())
                return false;
            distances.remove(moved);
            distances.add(index+1, moved);
        }
        return true;
    }
    
    /**
     * Renders the data on the main screen. Each distance is
     * represented by an HBox with the fields name, arrows to 
     * move up or down, a view button, and a remove button. 
     * 
     * @param distances the list of elements to be displayed
     * @param pane ScrollPane to stick HBoxes into
     * @param save Save file to be used if data is altered
     */
    public void render(ArrayList<Distance> distances, ScrollPane pane, SaveFile save){
        final VBox scrollPaneUsed = new VBox();//VBox to stick in ScrollPane
        distances.forEach(distance -> {
            Label name = new Label(distance.getName());
            name.setMinWidth(130);name.setMaxWidth(130);//Makes rows & columns line up
            
            //Move any given row up or down 1
            Button up = new Button("↑"); 
            up.setOnAction(e -> {if(move(distances, distance, 0)){render(distances, pane, save);}});
            Button down = new Button("↓"); 
            down.setOnAction(e -> {if(move(distances, distance, 1)){render(distances, pane, save);}});
            
            Button view = new Button("View");
            view.setOnAction(e -> distance.viewDistance(distances, save));
            Button remove = new Button("Remove");
            remove.setOnAction(e -> {
                boolean ok = false;
                if(distance.getRuns().size() > 0){//Check for error when deleting a table of data
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Warning!");
                    alert.setHeaderText("Warning!");
                    alert.setContentText("Removing this will delete all of its contents. Are you sure you want to do that?");
                    alert.showAndWait();
                    ok = alert.getResult() == ButtonType.OK;
                }
                if (distance.getRuns().isEmpty() || ok){
                    distances.remove(distance);
                    render(distances, pane, save);
                    save.saveToFile(distances);
                }
            });
            HBox row = new HBox(name, up, down, view, remove);
            scrollPaneUsed.getChildren().add(row);
        });
        pane.setContent(scrollPaneUsed);
    }
}
