package main;

import base.Ant;
import base.Cell;
import base.State;
import configuration.Configuration;
import enums.Movement;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.Arrays;
import java.util.List;

import static configuration.Configuration.CELL_SIZE;
import static configuration.Configuration.GRID_SIZE;

public class Controller {

    private int counter = 0;
    private int currentSpeed = 1000;
    private Cell[][] matrix = new Cell[GRID_SIZE][GRID_SIZE];
    private Thread antThread;

    @FXML
    private GridPane grid;

    @FXML
    private ComboBox<String> modeComboBox;

    @FXML
    private Label stepCounter;

    @FXML
    private Slider speedSlider;

    @FXML
    private Button startButton;

    @FXML
    private Button stopButton;

    @FXML
    private Button heatMapButton;

    @FXML
    private Button clearButton;

    public void initialize() {
        initModeComboBox();
        initGridConstraints();
        initGridCells();
        initSpeedSliderListener();
        repaintGridLines();
    }

    private void initModeComboBox() {
        ObservableList<String> modes = new ObservableListBase<String>() {
            private List<String> modes = Arrays.asList("RL", "RLR", "LLRR");

            @Override
            public String get(int index) {
                return modes.get(index);
            }

            @Override
            public int size() {
                return modes.size();
            }
        };
        modeComboBox.setItems(modes);
    }

    private void initGridConstraints() {
        for (int i = 0; i < GRID_SIZE; i++) {
            ColumnConstraints column = new ColumnConstraints(CELL_SIZE);
            grid.getColumnConstraints().add(column);

            RowConstraints row = new RowConstraints(CELL_SIZE);
            grid.getRowConstraints().add(row);
        }
    }

    private void initGridCells() {
        grid.getChildren().clear();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                matrix[i][j] = new Cell(i, j, CELL_SIZE);
                grid.add(matrix[i][j], i, j);
            }
        }
    }

    @FXML
    private void startSimulation() {
        if (modeComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Missing Field");
            alert.setHeaderText(null);
            alert.setContentText("Please select a simulation mode.");
            alert.showAndWait();
            return;
        }
        initStateList(modeComboBox.getValue());

        startButton.disableProperty().setValue(true);
        stopButton.disableProperty().setValue(false);
        modeComboBox.disableProperty().setValue(true);
        clearButton.disableProperty().setValue(true);

        Ant ant = new Ant(matrix[GRID_SIZE/2][GRID_SIZE/2], this, grid);
        antThread = new Thread(ant);
        antThread.start();
    }

    @FXML
    public void stopSimulation() {
        antThread.interrupt();
        stopButton.disableProperty().setValue(true);
        heatMapButton.disableProperty().setValue(false);
        clearButton.disableProperty().setValue(false);
    }

    @FXML
    private void startHeatMapShow() {
        heatMapButton.disableProperty().setValue(true);
        showHeatMap();
    }

    private void showHeatMap(){
        int maxVisitedCounter = getMaxVisitedCounter();
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                matrix[i][j].setCellHeatColor(maxVisitedCounter);
            }
        }
    }

    private int getMaxVisitedCounter(){
        int maxVisitedCounter =0;
        for (int i = 0; i < GRID_SIZE; i++)
            for (int j = 0; j < GRID_SIZE; j++)
                if (matrix[i][j].getVisitedCounter() > maxVisitedCounter)
                    maxVisitedCounter = matrix[i][j].getVisitedCounter();
        return  maxVisitedCounter;
    }

    private void initStateList(String movementString) {
        Configuration.instance.states.clear();
        String[] movements = movementString.split("");
        for (String movement : movements)
            if (movement.equals("R"))
                Configuration.instance.states.add(new State(Movement.RIGHT));
            else
                Configuration.instance.states.add(new State(Movement.LEFT));
    }

    public void repaintGridLines() {
        grid.setGridLinesVisible(false);
        grid.setGridLinesVisible(true);
    }

    public Cell getCell(int rowPos, int colPos) {
        //TODO: fix row/col pos
        return matrix[colPos][rowPos];
    }

    public void incStepCounter() {
        stepCounter.setText("Steps: " + ++counter);
    }

    private void initSpeedSliderListener() {
        speedSlider.valueProperty().addListener((observable, oldValue, newValue) -> currentSpeed = (int) (5 + (995 - 9.95*newValue.doubleValue())));
    }

    public int getCurrentSpeed() {
        return currentSpeed;
    }

    @FXML
    private void clearSimulation() {
        counter = 0;
        stepCounter.setText("Steps: " + counter);
        matrix = new Cell[GRID_SIZE][GRID_SIZE];
        initGridCells();
        repaintGridLines();

        startButton.disableProperty().setValue(false);
        stopButton.disableProperty().setValue(true);
        modeComboBox.disableProperty().setValue(false);
        heatMapButton.disableProperty().setValue(true);
    }

}
