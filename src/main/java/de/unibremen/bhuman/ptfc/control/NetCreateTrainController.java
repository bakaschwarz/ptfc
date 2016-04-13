package de.unibremen.bhuman.ptfc.control;

import com.sun.javafx.charts.Legend;
import de.unibremen.bhuman.ptfc.InfoWindow;
import de.unibremen.bhuman.ptfc.Main;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class NetCreateTrainController {

    @FXML
    private TextField networkField;

    @FXML
    private TextField trainField;

    @FXML
    private TextField testField;

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

    private File trainPath;

    private File fcnnPath;

    private File testPath;

    private SimpleBooleanProperty newNet;

    private String line;

    private Process train;

    private SimpleBooleanProperty inTraining;

    private XYChart.Series<Number, Number> series;

    private XYChart.Series<Number, Number> absoluteMin;

    private LineChart<Number, Number> lineChart;

    private NumberAxis xAxis, yAxis;

    private float minValueOfSeries;

    @FXML
    void initialize() {
        inTraining = new SimpleBooleanProperty(false);
        newNet = new SimpleBooleanProperty();
        loadFromConfig();
        prepareButtonBindings();
        prepareChartStuff();
    }

    void loadFromConfig() {
        File tmp;
        networkField.setText(Main.getObservableConfiguration().getString("networkPath", ""));
        tmp = new File(networkField.getText());
        networkPath = tmp.exists() ? tmp : null;
        trainField.setText(Main.getObservableConfiguration().getString("trainingPath", ""));
        tmp = new File(trainField.getText());
        trainPath = tmp.exists() ? tmp : null;
        fcnnField.setText(Main.getObservableConfiguration().getString("fcnnPath", ""));
        tmp = new File(fcnnField.getText());
        fcnnPath = tmp.exists() ? tmp : null;
        testField.setText(Main.getObservableConfiguration().getString("testPath", ""));
        tmp = new File(testField.getText());
        testPath = tmp.exists() ? tmp : null;
        mseField.setText(Main.getObservableConfiguration().getString("mse", "0.01"));
        epochesField.setText(Main.getObservableConfiguration().getString("epoches", "5000"));
        frequencyField.setText(Main.getObservableConfiguration().getString("frequency", "10"));
        learnField.setText(Main.getObservableConfiguration().getString("learnRate", "0.7"));
        layerField.setText(Main.getObservableConfiguration().getString("layers", "1024 24 1"));
    }

    void prepareButtonBindings() {
        cancelButton.disableProperty().bind(inTraining.not());
        testButton.disableProperty().bind(Bindings.or(inTraining, newNet));
        trainButton.disableProperty().bind(inTraining);
    }

    void prepareChartStuff() {
        xAxis = new NumberAxis();
        xAxis.setForceZeroInRange(false);
        yAxis = new NumberAxis();
        yAxis.setTickUnit(0.000001);
        xAxis.setLabel("Epoches");
        yAxis.setLabel("MSE");
        yAxis.setForceZeroInRange(false);
        lineChart = new LineChart<>(xAxis, yAxis);
        chartBox.getChildren().add(lineChart);
        lineChart.setPrefWidth(650);
    }

    void prepareNewChart() {
        lineChart.getData().clear();
        series = new XYChart.Series<>();
        series.setName("MSE Flow");
        absoluteMin = new XYChart.Series<>();
        absoluteMin.setName("Lowest MSE");
        lineChart.getData().addAll(series, absoluteMin);
        absoluteMin.getData().add(new XYChart.Data<Number, Number>(xAxis.getLowerBound(), minValueOfSeries));
        absoluteMin.getData().get(0).setNode(null);
        absoluteMin.getData().add(new XYChart.Data<Number, Number>(xAxis.getUpperBound(), minValueOfSeries));
        absoluteMin.getData().get(1).setNode(null);

        for (Node node : lineChart.getChildrenUnmodifiable()) {
            if (node instanceof Legend) {
                Legend legend = (Legend) node;
                for (Legend.LegendItem legendItem : legend.getItems()) {
                    if(legendItem.getText().equals(absoluteMin.getName())) {
                        Rectangle symbol = new Rectangle(10, 10);
                        symbol.setFill(Color.valueOf("#479bdd"));
                        legendItem.setSymbol(symbol);
                    }
                }
            }
        }
        minValueOfSeries = Float.MAX_VALUE;
    }

    @FXML
    void loadTrain(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File tmp = fileChooser.showOpenDialog(Main.getMainWindow());
        if(tmp != null && tmp.exists()) {
            trainPath = tmp;
            trainField.setText(trainPath.getAbsolutePath());
        }
    }

    @FXML
    void loadTest() {
        FileChooser fileChooser = new FileChooser();
        File tmp = fileChooser.showOpenDialog(Main.getMainWindow());
        if(tmp != null && tmp.exists()) {
            testPath = tmp;
            testField.setText(testPath.getAbsolutePath());
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
            prepareNewChart();
            consoleArea.setText("");
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
                                        currentMSELabel.setText(String.format(
                                                "Current Lowest MSE: %s",
                                                minValueOfSeries
                                        ));
                                        absoluteMin.getData().get(0).setYValue(minValueOfSeries);
                                        absoluteMin.getData().get(1).setYValue(minValueOfSeries);
                                    }
                                    absoluteMin.getData().get(0).setXValue(series.getData().get(0).getXValue());
                                    absoluteMin.getData().get(1).setXValue(series.getData().get(series.getData().size() - 1).getXValue());

                                    for(XYChart.Data<Number, Number> d : series.getData()) {
                                        if(yAxis.getLowerBound() > d.getYValue().doubleValue()) {
                                            yAxis.setLowerBound(d.getYValue().doubleValue());
                                        }
                                    }
                                }
                            });
                        }
                    }
                    Platform.runLater(() -> cleanUp("Finished the training."));
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
            cleanUp("Canceled by user!");
        }
    }

    @FXML
    void testNet() {
        if(allConditionsMet()) {

        } else {
            InfoWindow.showInfo("Error", "You did not provide all information needed.", Main.getMainWindow());
        }
    }

    boolean allConditionsMet() {
        boolean pathsProvided = fcnnPath != null && trainPath != null && networkPath != null;
        boolean fieldsFilled = !mseField.getText().equals("") && !epochesField.getText().equals("") && !frequencyField.getText().equals("");
        boolean layersSetWhenNew = !newNet.get() || !layerField.getText().equals("");
        return pathsProvided && fieldsFilled && layersSetWhenNew;
    }

    ArrayList<String> getArguments() {
        ArrayList<String> result = new ArrayList<>();
        result.add(fcnnPath.getAbsolutePath());
        result.add(String.format("-r %s", learnField.getText()));
        result.add(String.format("-n %s", networkPath.getAbsolutePath()));
        result.add(String.format("-t %s", trainPath.getAbsolutePath()));
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

    void cleanUp(String consoleMessage) {
        consoleArea.appendText(consoleMessage + "\n");
        currentMSELabel.setText("Current Lowest MSE: Infinite");
        inTraining.setValue(false);
        train = null;
    }

    @FXML
    void saveConfig() {
        Main.getObservableConfiguration().setStringProperty("networkPath", networkField.getText());
        Main.getObservableConfiguration().setStringProperty("trainingPath", trainField.getText());
        Main.getObservableConfiguration().setStringProperty("testPath", testField.getText());
        Main.getObservableConfiguration().setStringProperty("fcnnPath", fcnnField.getText());
        Main.getObservableConfiguration().setStringProperty("mse", mseField.getText());
        Main.getObservableConfiguration().setStringProperty("epoches", epochesField.getText());
        Main.getObservableConfiguration().setStringProperty("frequency", frequencyField.getText());
        Main.getObservableConfiguration().setStringProperty("learnRate", learnField.getText());
        Main.getObservableConfiguration().setStringProperty("layers", layerField.getText());
        InfoWindow.showInfo("Saved!", "Saved the configuration for future sessions!", Main.getMainWindow());
    }
}
