/*
 * David Graff 2018
 */
package running.tracker;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 *
 * @author david
 */
public class ProgressChart {
    private final LineChart<Number, Number> chart;
    private final String name;
    
    public ProgressChart(ArrayList<Run> data, String name){
        this.name = name;
        ObservableList<XYChart.Series<Number, Number>> series = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> seriesData = FXCollections.observableArrayList();
        data.forEach(run ->{
            seriesData.add(new XYChart.Data<>(run.getRealDate().getTime(), getTime(run)));
        });
        series.add(new XYChart.Series<>(seriesData));
        NumberAxis numberAxis = new NumberAxis();
        NumberAxis dateAxis = new NumberAxis();
        NumberFormat format = new DecimalFormat("#.#E0");
        dateAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                Date date = new Date(number.longValue());
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                return sdf.format(date);
            }

            @Override
            public Number fromString(String string) {
                try {
                    return format.parse(string);
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
        numberAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return getTime(number.intValue());
            }

            @Override
            public Number fromString(String string) {
                try {
                    return format.parse(string);
                } catch (ParseException e) {
                    return 0;
                }
            }
        });
        dateAxis.setForceZeroInRange(false);
        chart = new LineChart<>(dateAxis, numberAxis, series);
        chart.setLegendVisible(false);
    }
    
    /**
     * Converts the time into seconds for convenience
     * @param input
     * @return 
     */
    private double getTime(Run input){
        return input.getMinutes()*60 + input.getSeconds();
    }
    
    public void displayChart(){
        Stage newStage = new Stage();
        newStage.setTitle(name);
        Button close = new Button("Close");
        close.setOnAction(e -> newStage.close()); close.setCancelButton(true);
        ToolBar toolbar = new ToolBar(close);
        Scene scene = new Scene(new VBox(chart, toolbar));
        newStage.setScene(scene);
        newStage.showAndWait();
    }
    
    /**
     * Returns the run time as a string in XX:XX:XX.XX format, 
     * removing unnecessary pieces.
     * @param seconds
     * @return time time
     */
    public String getTime(double seconds){
        int minutes = 0;
        if(seconds > 60){
            minutes =(int) seconds / 60;
            seconds -= minutes * 60;
        }
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
}
