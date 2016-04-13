package de.unibremen.bhuman.ptfc.control;

import de.unibremen.bhuman.ptfc.InfoWindow;
import de.unibremen.bhuman.ptfc.Main;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;

public class NetCreateTrainController {

    @FXML
    private TextField networkField;

    @FXML
    private TextField trainField;

    @FXML
    private TextField mseField;

    @FXML
    private TextField epochesField;

    @FXML
    private TextField frequencyField;

    @FXML
    private TextField layerField;

    @FXML
    private TextField fcnnField;

    @FXML
    private TextField learnField;

    @FXML
    private TextArea consoleArea;

    @FXML
    private Button trainButton;

    @FXML
    private Button testButton;

    @FXML
    private Button cancelButton;

    @FXML
    private VBox chartBox;

    @FXML
    private Label currentMSELabel;

    private File networkPath;

    private File datasetPath;

    private File fcnnPath;

    private SimpleBooleanProperty newNet;

    private String line;

    private Process train;

    private SimpleBooleanProperty inTraining;

    private XYChart.Series<Number, Number> series;

    private XYChart.Series<Number, Number> absoluteMin;

    private LineChart<Number, Number> lineChart;

    private NumberAxis xAxis, yAxis;

    float minValueOfSeries;

    @FXML
    void initialize() {
        inTraining = new SimpleBooleanProperty(false);
        newNet = new SimpleBooleanProperty();
        cancelButton.disableProperty().bind(inTraining.not());
        testButton.disableProperty().bind(Bindings.or(inTraining, newNet));
        trainButton.disableProperty().bind(inTraining);
        xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        yAxis = new NumberAxis();
        yAxis.setTickUnit(0.000001);
        xAxis.setLabel("Epoches");
        yAxis.setLabel("MSE");
        yAxis.setForceZeroInRange(false);
        lineChart = new LineChart<>(xAxis, yAxis);
        chartBox.getChildren().add(lineChart);
        series = new XYChart.Series<>();
        series.setName("MSE Flow");
        absoluteMin = new XYChart.Series<>();
        absoluteMin.setName("Lowest Value");
        lineChart.getData().addAll(series, absoluteMin);
        lineChart.setPrefWidth(650);
        minValueOfSeries = Float.MAX_VALUE;
    }

    @FXML
    void loadDataset(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File tmp = fileChooser.showOpenDialog(Main.getMainWindow());
        if(tmp != null && tmp.exists()) {
            datasetPath = tmp;
            trainField.setText(datasetPath.getAbsolutePath());
        }
    }

    @FXML
    void loadFCNNExe(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File tmp = fileChooser.showOpenDialog(Main.getMainWindow());
        if(tmp != null && tmp.exists()) {
            fcnnPath = tmp;
            fcnnField.setText(fcnnPath.getAbsolutePath());
        }
    }

    @FXML
    void loadNet(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File tmp = fileChooser.showOpenDialog(Main.getMainWindow());
        if(tmp != null && tmp.exists()) {
            newNet.set(false);
            networkPath = tmp;
            networkField.setText(networkPath.getAbsolutePath());
        }
    }

    @FXML
    void newNet(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/newnetdialog.fxml"));
        Parent node = fxmlLoader.load();
        Scene scene = new Scene(node);
        scene.getStylesheets().add(Main.class.getResource("style/main.css").toExternalForm());
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(Main.getMainWindow());
        stage.showAndWait();
        if(!NewNetworkController.isCanceled() && !NewNetworkController.getFilename().equals("") && NewNetworkController.getSaveDirectory() != null) {
            networkPath = new File(
                    NewNetworkController.getSaveDirectory().getAbsolutePath() + File.separator +
                            NewNetworkController.getFilename());
            networkField.setText(networkPath.getAbsolutePath());
            newNet.set(true);
        }
    }

    @FXML
    void trainNet() throws IOException {
        if(allConditionsMet()) {
            series.getData().clear();
            Thread thread = new Thread(new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    train = new ProcessBuilder(getArguments()).redirectErrorStream(true).start();
                    inTraining.setValue(true);
                    InputStreamReader reader = new InputStreamReader(train.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    while(train.isAlive()) {
                        if ((line = bufferedReader.readLine()) != null) {
                            Platform.runLater(() -> {
                                consoleArea.appendText(line + "\n");
                                if (line.toCharArray()[0] == 'b') {
                                    XYChart.Data<Number, Number> data = new XYChart.Data<>(Integer.parseInt(line.split(" ")[2].replace(",", "")), Float.parseFloat(line.split(" ")[4]));
                                    series.getData().add(data);

                                    if (series.getData().size() > 20) {
                                        series.getData().remove(0, 1);
                                        xAxis.setTickUnit(20);
                                        xAxis.setLowerBound(series.getData().get(0).getXValue().floatValue());
                                        xAxis.setUpperBound(series.getData().get(series.getData().size() -1 ).getXValue().floatValue());
                                    }

                                    if (series.getData().get(series.getData().size() -1).getYValue().floatValue() < minValueOfSeries) {
                                        minValueOfSeries = series.getData().get(series.getData().size() -1).getYValue().floatValue();
                                        absoluteMin.getData().clear();
                                        absoluteMin.getData().add(new XYChart.Data<Number, Number>(xAxis.getLowerBound(), minValueOfSeries));
                                        absoluteMin.getData().add(new XYChart.Data<Number, Number>(xAxis.getUpperBound(), minValueOfSeries));
                                        currentMSELabel.setText(String.format(
                                                "Current Lowest MSE: %s",
                                                minValueOfSeries
                                        ));
                                    }

                                    for(XYChart.Data<Number, Number> d : series.getData()) {
                                        if(yAxis.getLowerBound() > d.getYValue().doubleValue()) {
                                            yAxis.setLowerBound(d.getYValue().doubleValue());
                                        }
                                    }
                                }
                            });
                        }
                    }
                    inTraining.setValue(false);
                    minValueOfSeries = Float.MAX_VALUE;
                    currentMSELabel.setText("Current Lowest MSE: Infinite");
                    return null;
                }
            });
            thread.setUncaughtExceptionHandler((t, e) -> e.printStackTrace());
            thread.setDaemon(true);
            thread.start();
        } else {
            InfoWindow.showInfo("Error", "You did not provide all information needed.", Main.getMainWindow());
        }
    }

    @FXML
    void cancelTraining() {
        if(train != null && InfoWindow.showConfirm("Need Confirmation", "Do you really want to cancel the training? All progress will be lost.", Main.getMainWindow())) {
            train.destroyForcibly();
            train = null;
            inTraining.setValue(false);
        }
    }

    @FXML
    void testNet() {
        if(allConditionsMet()) {

        } else {
            InfoWindow.showInfo("Error", "You did not provide all information needed.", Main.getMainWindow());
        }
    }

    private boolean allConditionsMet() {
        boolean pathsProvided = fcnnPath != null && datasetPath != null && networkPath != null;
        boolean fieldsFilled = !mseField.getText().equals("") && !epochesField.getText().equals("") && !frequencyField.getText().equals("");
        boolean layersSetWhenNew = !newNet.get() || !layerField.getText().equals("");
        return pathsProvided && fieldsFilled && layersSetWhenNew;
    }

    private ArrayList<String> getArguments() {
        ArrayList<String> result = new ArrayList<>();
        result.add(fcnnPath.getAbsolutePath());
        result.add(String.format("-r %s", learnField.getText()));
        result.add(String.format("-n %s", networkPath.getAbsolutePath()));
        result.add(String.format("-t %s", datasetPath.getAbsolutePath()));
        if(newNet.get()) {
            for (String layer : layerField.getText().split(" ")) {
                result.add(String.format("-l %s", layer));
            }
        }
        result.add(String.format("-e %s", mseField.getText()));
        result.add(String.format("-m %s", epochesField.getText()));
        result.add(String.format("-f %s", frequencyField.getText()));
        return result;
    }

}
