/*
 * David Graff 2018
 */
package running.tracker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author david
 */
public class Run implements Serializable{
    private int minutes;
    private double seconds;
    private Date date;

    public Run(int minutes, double seconds, Date date){
        this.minutes = minutes;
        this.seconds = seconds;
        this.date = date;
    }
    
    /**
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * @param minutes the minutes to set
     */
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * @return the seconds
     */
    public double getSeconds() {
        return seconds;
    }

    /**
     * @param seconds the seconds to set
     */
    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    /**
     * @return the given date expressed in MM/dd/yyyy format
     */
    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        return sdf.format(date);
    }
    
    public Date getRealDate(){
        return date;
    }

    /**
     * Sets the date for a given run. NOT ZERO INDEXED
     * 
     * @param year
     * @param month
     * @param day 
     */
    public void setDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month-1, day);
        date = cal.getTime();
    }
    
    /**
     * Returns the run time as a string in XX:XX:XX.XX format, 
     * removing unnecessary pieces.
     * @return time time
     */
    public String getTime(){
        String result = "";
        int minRemaining = minutes;
        if(minutes / 60 > 0){//time is at least an hour
            result = result.concat(Integer.toString(minutes/60) + ":");
            minRemaining = minutes % 60;
        }
        if(minRemaining < 10 && minutes > 10)
            result = result.concat("0");
        result = result.concat(Integer.toString(minRemaining) + ":");
        if(seconds < 10)
            result = result.concat("0");
        if(seconds % 1 == 0)
            result = result.concat(Integer.toString((int)seconds) + "  ");
        else
            result = result.concat(Double.toString(seconds));
        return result;
    }
    
    /**
     * Displays a stage to edit a given runs time or date
     * @param saveFile 
     */
    public void editRun(SaveFile saveFile){
        Stage thisStage = new Stage();
        thisStage.setTitle("Add Run");
        String[] currentDate = getDate().split("/");
        
        TextField month = new TextField(currentDate[0]); month.setMaxWidth(40);
        TextField day = new TextField(currentDate[1]); day.setMaxWidth(40);
        TextField year = new TextField(currentDate[2]); year.setMaxWidth(55);
        HBox date = new HBox(new Label("Month: "), month, new Label("Day: "), day, new Label("Year: "), year);
        TextField min = new TextField(Integer.toString(minutes)); min.setMaxWidth(40);
        TextField sec = new TextField(Double.toString(seconds)); sec.setMaxWidth(50);
        HBox time = new HBox(new Label("Minutes: "), min, new Label("Seconds: "), sec);
        
        Label error = new Label();
        Button cancel = new Button("Cancel");cancel.setCancelButton(true);
        cancel.setOnAction(e -> thisStage.close());
        Button save = new Button("Save");save.setDefaultButton(true);
        save.setOnAction(e ->{
            try{
                int m = Integer.parseInt(month.getText());
                int d = Integer.parseInt(day.getText());
                int y = Integer.parseInt(year.getText());
                int mt = Integer.parseInt(min.getText());
                double st = Double.parseDouble(sec.getText());
                if(m < 1 || m > 12 || m > 31 || d < 1 || y < 1 || mt < 0 || (mt < 1 && st == 0) || st >= 60 || st < 0)
                    throw new NumberFormatException();
                setDate(y, m, d);
                minutes = mt;
                seconds = st;
                saveFile.saveToFile();
                thisStage.close();
            } catch(NumberFormatException x){
                error.setText("Please enter a valid input!");
            }
        });
        HBox control = new HBox(save, cancel, error);
        control.setMinWidth(320);
        Scene scene = new Scene(new VBox(date, time, control));
        thisStage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource("RunningStyle.css").toExternalForm());
        thisStage.showAndWait();
    }
}
