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

import org.example.vocabhub.config.AppConfig;
import org.example.vocabhub.statistics.StatisticData;
import org.example.vocabhub.statistics.StatisticDataBinder;
import org.example.vocabhub.trainer.*;
import org.example.vocabhub.persistence.PersistentFileService;
import org.example.vocabhub.trainer.model.CheckVocabularyAnswer;
import org.example.vocabhub.trainer.model.VocabularyPair;
import org.example.vocabhub.trainer.model.VocabularySet;
import org.example.vocabhub.trainer.strategies.RandomSelectionStrategy;
import org.example.vocabhub.utils.VocableTableViewItem;


import java.util.logging.Logger;
import java.util.logging.Level;

public class Controller implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private StatisticDataBinder statisticDataBinder = new StatisticDataBinder();

    // general
    @FXML private MenuItem uiMenuItem_closeFile;
    @FXML private MenuItem uiMenuItem_exportVocab;
    @FXML private MenuItem uiMenuItem_saveVocab;
    // learn vocab
    @FXML private Label uiLabel_loadedFile;
    @FXML private Label uiLabel_vocabPercentage;
    @FXML private Label uiLabel_currentVocable;
    @FXML private Label uiLabel_correctVocab;
    @FXML private Label uiLabel_languages;
    @FXML private Button uiButton_submitVocab;
    @FXML private Button uiButton_nextVocab;
    @FXML private TextField uiTextInput_vocab;
    // add vocab
    @FXML private TextField uiTextInput_addVocabKey;
    @FXML private TextField uiTextInput_addVocabValue;
    @FXML private TableView<VocableTableViewItem> uiTableView_newVocab;
    @FXML private TableColumn uiTableColumn_key;
    @FXML private TableColumn uiTableColumn_value;
    @FXML private ChoiceBox uiChoiceBox_translation;
    @FXML private ChoiceBox uiChoiceBox_baseLanguage;
    // statistics
    @FXML private BarChart uiBarChart_mistakes;
    @FXML private CategoryAxis uiCategoryAxis_statisticsMistakes;
    @FXML private NumberAxis uiNumberAxis_statisticsMistakes;
    @FXML private Label uiLabel_vocabTrainedTotal;
    @FXML private Label uiLabel_vocabWrongTotal;
    @FXML private Label uiLabel_rightWrongAverage;

    XYChart.Series dataSeriesIssues = new XYChart.Series();

    private final PersistentFileService<VocabularySet> vocabularyFileService = new PersistentFileService<>(VocabularySet.class);
    private final PersistentFileService<StatisticDataBinder> statisticsFileService = new PersistentFileService<>(StatisticDataBinder.class);
    private final AppConfig appConfig = new AppConfig();
    private VocabularySet vocabularies = new VocabularySet();
    private VocabularyTrainer vocabularyTrainer;
    private File selectedFile;

    public Controller() throws IOException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LOGGER.log(Level.INFO, "Initializing controller...");

        closeFile();
        initializeChoiceBoxes();

        uiTableColumn_key.setCellValueFactory(new PropertyValueFactory<>("key"));
        uiTableColumn_value.setCellValueFactory(new PropertyValueFactory<>("value"));

        updateStatistics();

        LOGGER.log(Level.INFO, "Initializing statisticDataBinder...");
        Optional<StatisticDataBinder> statisticDataBinderOptional = this.statisticsFileService.loadFromFile(this.appConfig.getStatisticsFilePath());
        statisticDataBinderOptional.ifPresent(dataBinder -> this.statisticDataBinder = dataBinder);
    }

    @FXML
    protected void onUiMenuItem_openFile() {
        LOGGER.log(Level.INFO, "Menu button open file triggered...");

        // Get File...
        FileChooser fileChooser = createFileChooser("Open Vocab File");
        selectedFile = fileChooser.showOpenDialog(new Stage());

        // Read File...
        if(selectedFile.isFile()) {
            closeFile();
            String selectedFileName = selectedFile.toString();
            uiLabel_loadedFile.setText("Loaded File: " + selectedFileName);
            Optional<VocabularySet> result = this.vocabularyFileService.loadFromFile(selectedFile.toPath());
            this.vocabularyFileService.loadFromFile(selectedFile.toPath()).ifPresent(vocabularyFile -> this.vocabularies = vocabularyFile);
            vocabularyTrainer = new VocabularyTrainer(vocabularies, new RandomSelectionStrategy());
            setAllLanguages(vocabularies.getSourceLanguage(), vocabularies.getTargetLanguage());
            for( VocabularyPair pair : vocabularies.getVocabularies()){
                addNewVocableLine(pair.getSource(), pair.getTarget());
            }
            // Start vocab question cycle
            uiButton_submitVocab.setDisable(false);
            uiMenuItem_closeFile.setDisable(false);
            uiMenuItem_saveVocab.setDisable(false);
            uiMenuItem_exportVocab.setDisable(false);
            uiLabel_vocabPercentage.setText(vocabularyTrainer.getLearnedSize() + "/" + vocabularies.getSize());
            takeNextVocabulary();
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
        String userInput = uiTextInput_vocab.getText();
        CheckVocabularyAnswer answer = vocabularyTrainer.checkVocabulary(userInput);
        if (answer.isCorrect()) {
           updateUiAfterCorrectVocabulary();
        } else {
            updateUiAfterWrongVocabulary(answer);
        }
        closeTrainingIfDone();
        setLearnUiItemsDisabled(true);
    }

    private void takeNextVocabulary() {
        if (vocabularyTrainer.currentVocabulary().isPresent()) {
            LOGGER.log(Level.INFO, "take next vocabulary...");
            uiTextInput_vocab.clear();
            uiLabel_currentVocable.setText(vocabularyTrainer.currentVocabulary().get());
            uiLabel_correctVocab.setText("");
            setLearnUiItemsDisabled(false);
        }
    }

    private void updateUiAfterCorrectVocabulary() {
        uiLabel_correctVocab.setText("That's correct!");
        uiLabel_vocabPercentage.setText(vocabularyTrainer.getLearnedSize() + "/" + vocabularyTrainer.getOverallSize());
    }

    private void updateUiAfterWrongVocabulary(CheckVocabularyAnswer answer) {
        uiLabel_correctVocab.setText("That's incorrect! It's: " + answer.getRightAnswer());
    }

    @FXML
    protected void onUiButton_nextVocab() {
        if(vocabularyTrainer.finished()) {
            vocabularyTrainer = new VocabularyTrainer(vocabularies, new RandomSelectionStrategy());
            uiLabel_vocabPercentage.setText(vocabularyTrainer.getLearnedSize() + "/" + vocabularyTrainer.getOverallSize());
            takeNextVocabulary();
        } else {
            takeNextVocabulary();
        }
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
            vocabularies.addVocabularyPair(new VocabularyPair(key, value));
            uiTextInput_addVocabKey.clear();
            uiTextInput_addVocabValue.clear();
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
        this.vocabularyFileService.saveToFile(selectedFile.toPath(), vocabularies);
    }

    @FXML
    protected void onUiMenuItem_exportVocab() throws IOException {
        LOGGER.log(Level.INFO, "Export vocable...");

        FileChooser fileChooser = createFileChooser("Export Vocab File");
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        LOGGER.log(Level.INFO, "Creating file to export to...");
        try {
            if (selectedFile.createNewFile()) {
                LOGGER.log(Level.INFO, "File created: " + selectedFile.toString());
            } else {
                LOGGER.log(Level.INFO, "File already exists!");
            }
            LOGGER.log(Level.INFO, "Writing to file...");
            this.vocabularyFileService.saveToFile(selectedFile.toPath(), vocabularies);
        } catch (IOException error) {
            LOGGER.log(Level.SEVERE, "An error has occurred while creating the file: " + selectedFile.toString());
            LOGGER.log(Level.SEVERE, "STACKTRACE: \n" + error);
        }
    }

    @FXML protected void onUiMenuItem_about() {
        LOGGER.log(Level.INFO, "Open about form...");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(480, 20);
        alert.setTitle("VocabHub ~ ABOUT");
        alert.setHeaderText("This app was made by Patrick Schulze");
        alert.setContentText("For further Information contact me at: Patrick.Schulze.ofp@gmail.com");

        alert.showAndWait();
    }

    public void initializeChoiceBoxes() {
        LOGGER.log(Level.INFO, "Initialize choiceboxes...");
        List<String> languages = appConfig.getLanguages();


        for(int index = 0; index < languages.size(); index++) {
            uiChoiceBox_baseLanguage.getItems().add(languages.get(index));
            uiChoiceBox_translation.getItems().add(languages.get(index));
        }

        uiChoiceBox_baseLanguage.setOnAction((event) -> {onLanguageChoiceBoxChanged();});
        uiChoiceBox_translation.setOnAction((event) -> {onLanguageChoiceBoxChanged();});
    }

    public void onLanguageChoiceBoxChanged() {
        LOGGER.log(Level.INFO, "Language choicebox changed...");
        setAllLanguages((String) uiChoiceBox_baseLanguage.getValue(), (String) uiChoiceBox_translation.getValue());
        vocabularies.setSourceLanguage(uiChoiceBox_baseLanguage.getValue().toString());
        vocabularies.setTargetLanguage(uiChoiceBox_translation.getValue().toString());
    }

    public void setAllLanguages(String baseLanguage, String translation) {
        LOGGER.log(Level.INFO, "Set all languages correctly...");
        if(baseLanguage.length() != 0 || translation.length() != 0) {
            uiLabel_languages.setText(baseLanguage + " ➡ " + translation);
            uiTableColumn_key.setText(baseLanguage);
            uiTableColumn_value.setText(translation);
            uiChoiceBox_baseLanguage.setValue(baseLanguage);
            uiChoiceBox_translation.setValue(translation);
        } else {
            clearAllLanguages();
        }
    }

    public void clearAllLanguages() {
        LOGGER.log(Level.INFO, "Clear all language ui items...");
        uiLabel_languages.setText("");
        uiTableColumn_key.setText("Key");
        uiTableColumn_value.setText("Value");
        uiChoiceBox_baseLanguage.setValue("");
        uiChoiceBox_translation.setValue("");
    }

    private void closeTrainingIfDone() {
        if (vocabularyTrainer.finished()) {
            uiLabel_currentVocable.setText("");
            uiLabel_correctVocab.setText("Congrats! You're done! You made " + vocabularyTrainer.getFailedSize() + " mistake(s)!");
            uiLabel_vocabPercentage.setText("0/0");

            this.statisticDataBinder.addData(new StatisticData(
                    Optional.of(vocabularyTrainer.getFailedSize()),
                    Optional.of(vocabularyTrainer.getLearnedSize()))
            );
            this.statisticsFileService.saveToFile(this.appConfig.getStatisticsFilePath(), this.statisticDataBinder);
            updateStatistics();

            uiButton_nextVocab.setVisible(true);
        }
    }

    public void closeFile() {
        LOGGER.log(Level.INFO, "Close file triggered...");
        uiLabel_loadedFile.setText("Loaded File: None");
        uiLabel_vocabPercentage.setText("0/0");
        uiLabel_currentVocable.setText("");
        uiLabel_correctVocab.setText("");

        uiButton_nextVocab.setVisible(false);
        uiMenuItem_closeFile.setDisable(false);
        uiButton_submitVocab.setDisable(true);
        uiMenuItem_closeFile.setDisable(true);
        uiMenuItem_exportVocab.setDisable(true);
        uiMenuItem_saveVocab.setDisable(true);

        uiTextInput_vocab.clear();
        uiTableView_newVocab.getItems().clear();

        clearAllLanguages();
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
        int totalTrained =  this.statisticDataBinder.getTotalCorrectCount();
        int totalMistakes = this.statisticDataBinder.getTotalMistakeCount();

        uiLabel_vocabTrainedTotal.setText("Total Vocable trained: " + totalTrained);
        uiLabel_vocabWrongTotal.setText("Total Vocable wrong: " + totalMistakes);
        if(totalTrained != 0 && totalMistakes != 0) {
            uiLabel_rightWrongAverage.setText("R/W: " + (totalTrained/totalMistakes));
        } else {
            uiLabel_rightWrongAverage.setText("R/W: " + 1);
        }

        //dataSeriesIssues.getData().removeAll();
        uiBarChart_mistakes.getData().clear();
        uiBarChart_mistakes.setLegendVisible(false);

        ArrayList<String> dates = this.statisticDataBinder.getDates();
        for(int index = 0; index < dates.size(); index++) {
            dataSeriesIssues.getData().add(
                    new XYChart.Data(
                            dates.get(index),
                            this.statisticDataBinder.getMistakeCountByDate(dates.get(index)
                            )
                    )
            );
        }
        uiBarChart_mistakes.getData().add(dataSeriesIssues);
    }
}