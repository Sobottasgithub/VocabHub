package org.example.vocabhub;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.XYChart;

import org.example.vocabhub.utils.Data;
import org.example.vocabhub.utils.StatisticsData;
import org.example.vocabhub.utils.VocableTableViewItem;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Controller implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

    // general
    @FXML private MenuItem uiMenuItem_closeFile;
    @FXML private MenuItem uiMenuItem_exportVocab;
    @FXML private MenuItem uiMenuItem_saveVocab;
    // learn vocab
    @FXML private Label uiLabel_loadedFile;
    @FXML private Label uiLabel_vocabPercentage;
    @FXML private Label uiLabel_currentVocable;
    @FXML private Label uiLabel_correctVocab;
    @FXML private Button uiButton_submitVocab;
    @FXML private Button uiButton_nextVocab;
    @FXML private TextField uiTextInput_vocab;
    // add vocab
    @FXML private TextField uiTextInput_addVocabKey;
    @FXML private TextField uiTextInput_addVocabValue;
    @FXML private TableView<VocableTableViewItem> uiTableView_newVocab;
    @FXML private TableColumn uiTableColumn_key;
    @FXML private TableColumn uiTableColumn_value;
    // statistics
    @FXML private BarChart uiBarChart_mistakes;
    @FXML private CategoryAxis uiCategoryAxis_statisticsMistakes;
    @FXML private NumberAxis uiNumberAxis_statisticsMistakes;
    @FXML private Label uiLabel_vocabTrainedTotal;
    @FXML private Label uiLabel_vocabWrongTotal;
    @FXML private Label uiLabel_rightWrongAverage;


    int mistakes = 0;
    int randomVocabIndex = -1;
    List correctAnswers = new ArrayList<Integer>();
    Data data = new Data();
    StatisticsData statisticsData = new StatisticsData();
    XYChart.Series dataSeriesIssues = new XYChart.Series();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing controller...");
        closeFile();

        uiTableColumn_key.setCellValueFactory(new PropertyValueFactory<>("key"));
        uiTableColumn_value.setCellValueFactory(new PropertyValueFactory<>("value"));

        updateStatistics();

        //SEVERE
        //WARNING
        //INFO
        //CONFIG
        //FINE
        //FINER
        //FINEST
    }

    @FXML
    protected void onUiMenuItem_openFile() {
        LOGGER.log(Level.INFO, "Menu button open file triggered...");

        // Get File...
        FileChooser fileChooser = createFileChooser("Open Vocab File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        // Read File...
        if(selectedFile.isFile()) {
            closeFile();
            String selectedFileName = selectedFile.toString();
            uiLabel_loadedFile.setText("Loaded File: " + selectedFileName);
            data = new Data(selectedFileName);
        }

        // Start vocab question cycle
        uiButton_submitVocab.setDisable(false);
        uiMenuItem_closeFile.setDisable(false);
        uiMenuItem_saveVocab.setDisable(false);
        uiMenuItem_exportVocab.setDisable(false);
        uiLabel_vocabPercentage.setText(correctAnswers.size() + "/" + data.getSize());
        nextRandomVocable();

        for(int index = 0; index < data.getSize(); index++) {
            String key = data.getKeyByIndex(index);
            String value = data.getValue(key);
            addNewVocableLine(key, value);
        }
    }

    @FXML
    protected void onUiMenuItem_closeFile() {
        LOGGER.log(Level.INFO, "Menu button close file triggered...");
        closeFile();
    }

    @FXML
    protected void onUiButton_submitVocab() {
        LOGGER.log(Level.INFO, "Button submit vocab triggered...");
        nextRandomVocable();
        String uiLabel_currentVocable = data.getValue(data.getKeyByIndex(randomVocabIndex));
        if (uiTextInput_vocab.getText().equals(uiLabel_currentVocable)) {
            correctAnswers.add(randomVocabIndex);
            uiLabel_correctVocab.setText("That's correct!");
            uiLabel_vocabPercentage.setText(correctAnswers.size() + "/" + data.getSize());
        } else {
            mistakes++;
            uiLabel_correctVocab.setText("That's incorrect! It's: " + uiLabel_currentVocable);
        }
        setLearnUiItemsDisabled(true);
    }

    @FXML
    protected void onUiButton_nextVocab() {
        LOGGER.log(Level.INFO, "Button next vocab triggered...");
        uiTextInput_vocab.clear();
        uiLabel_correctVocab.setText("");
        setLearnUiItemsDisabled(false);

        if (correctAnswers.size() == data.getSize() && randomVocabIndex == -1) {
            uiLabel_vocabPercentage.setText("0/0");

            mistakes = 0;
            correctAnswers = new ArrayList<Integer>();
        }

        nextRandomVocable();
    }

    @FXML
    protected void onUiButton_addVocab() {
        LOGGER.log(Level.INFO, "Adding vocab to TableView...");
        uiTextInput_addVocabKey.setStyle("");
        uiTextInput_addVocabValue.setStyle("");
        String key = uiTextInput_addVocabKey.getText();
        String value = uiTextInput_addVocabValue.getText();
        if(!key.isEmpty() && !value.isEmpty()) {
            addNewVocableLine(key, value);
            data.put(key, value);
            uiLabel_vocabPercentage.setText(correctAnswers.size() + "/" + data.getSize());
            uiTextInput_addVocabKey.clear();
            uiTextInput_addVocabValue.clear();
            nextRandomVocable();
            uiMenuItem_exportVocab.setDisable(false);
            setLearnUiItemsDisabled(false);
        } else {
            if(key.isEmpty()) {
                uiTextInput_addVocabKey.setStyle("-fx-background-color: #ff000066;");
            }
            if(value.isEmpty()) {
                uiTextInput_addVocabValue.setStyle("-fx-background-color: #ff000066;");
            }
        }
    }

    @FXML
    protected void onUiMenuItem_saveVocab() {
        LOGGER.log(Level.INFO, "Save vocable...");
        data.saveToFile("");
    }

    @FXML
    protected void onUiMenuItem_exportVocab() throws IOException {
        LOGGER.log(Level.INFO, "Export vocable...");

        FileChooser fileChooser = createFileChooser("Export Vocab File");
        File selectedFile = fileChooser.showSaveDialog(new Stage());
        selectedFile.createNewFile();

        LOGGER.log(Level.INFO, "Creating file to export to...");
        try {
            if (selectedFile.createNewFile()) {
                LOGGER.log(Level.INFO, "File created: " + selectedFile.toString());
            } else {
                LOGGER.log(Level.INFO, "File already exists!");
            }
            LOGGER.log(Level.INFO, "Writing to file...");
            data.saveToFile(selectedFile.toString());
        } catch (IOException error) {
            LOGGER.log(Level.SEVERE, "An error has occurred while creating the file: " + selectedFile.toString());
            LOGGER.log(Level.SEVERE, "STACKTRACE: \n" + error);
        }
    }

    @FXML protected void onUiMenuItem_about() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("VocabHub ~ ABOUT");
        alert.setHeaderText("This app was made by Patrick Schulze");
        alert.setContentText("For further Information contact me at: Patrick.Schulze.ofp@gmail.com");

        alert.showAndWait();
    }

    public void nextRandomVocable() {
        LOGGER.log(Level.INFO, "Generating next random vocable...");
        if (correctAnswers.size() == data.getSize()) {
            uiLabel_currentVocable.setText("");
            uiLabel_correctVocab.setText("Congrats! You're done! You made " + mistakes + " mistake(s)!");
            setLearnUiItemsDisabled(true);
            randomVocabIndex = -1;

            statisticsData.addMistakeWithDate(mistakes);
            statisticsData.addTrainedCount(data.getSize());
            updateStatistics();
            return;
        }
        while (correctAnswers.contains(randomVocabIndex) || randomVocabIndex == -1) {
            Random random = new Random();
            randomVocabIndex = random.nextInt(data.getSize());
        }

        uiLabel_currentVocable.setText("" + data.getKeyByIndex(randomVocabIndex));
    }

    public void closeFile() {
        LOGGER.log(Level.INFO, "Close file triggered...");
        uiLabel_loadedFile.setText("Loaded File: None");
        uiLabel_vocabPercentage.setText("0/0");
        uiLabel_currentVocable.setText("");
        uiLabel_correctVocab.setText("");

        randomVocabIndex = -1;
        data = new Data();
        correctAnswers = new ArrayList<Integer>();

        uiButton_nextVocab.setVisible(false);
        uiMenuItem_closeFile.setDisable(false);
        uiButton_submitVocab.setDisable(true);
        uiMenuItem_closeFile.setDisable(true);
        uiMenuItem_exportVocab.setDisable(true);
        uiMenuItem_saveVocab.setDisable(true);

        uiTextInput_vocab.clear();
        uiTableView_newVocab.getItems().clear();
    }

    public void addNewVocableLine(String key, String value) {
        LOGGER.log(Level.INFO, "Adding new vocable line...");
        uiTableView_newVocab.getItems().add(new VocableTableViewItem(key, value));
    }

    public FileChooser createFileChooser(String title) {
        LOGGER.log(Level.INFO, "Creating file chooser...");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        FileChooser.ExtensionFilter fileExtensions =
                new FileChooser.ExtensionFilter(
                        "Vocab Files", "*.json");
        fileChooser.getExtensionFilters().add(fileExtensions);
        return fileChooser;
    }

    public void setLearnUiItemsDisabled(boolean bool) {
        uiButton_nextVocab.setVisible(bool);
        uiButton_submitVocab.setDisable(bool);
        uiTextInput_vocab.setDisable(bool);
    }

    public void updateStatistics() {
        LOGGER.log(Level.INFO, "Update statistics...");
        int totalTrained =  statisticsData.getTrainedCount();
        int totalMistakes = statisticsData.getTotalMistakes();
        HashMap<String, Integer> mistakesWithDate = statisticsData.getMistakesWithDate();

        uiLabel_vocabTrainedTotal.setText("Total Vocable trained: " + totalTrained);
        uiLabel_vocabWrongTotal.setText("Total Vocable wrong: " + totalMistakes);
        if(totalTrained != 0 && totalMistakes != 0) {
            uiLabel_rightWrongAverage.setText("R/W: " + (totalTrained/totalMistakes));
        } else {
            uiLabel_rightWrongAverage.setText("R/W: " + 1);
        }

        dataSeriesIssues.getData().clear();
        uiBarChart_mistakes.setLegendVisible(false);
        for(int index = 0; index < mistakesWithDate.size(); index++) {
            String date = mistakesWithDate.keySet().toArray()[index].toString();
            dataSeriesIssues.getData().add(new XYChart.Data(date, mistakesWithDate.get(date)));
        }
        uiBarChart_mistakes.getData().add(dataSeriesIssues);
    }
}