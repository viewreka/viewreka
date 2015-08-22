package org.beryx.viewreka.fxapp;

import static org.beryx.viewreka.core.Util.checkNotNull;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import org.beryx.viewreka.chart.ChartController;
import org.beryx.viewreka.fxapp.export.FramesPerSecond;
import org.beryx.viewreka.fxapp.export.GifSequenceWriter;
import org.beryx.viewreka.fxapp.export.MP4Creator;
import org.beryx.viewreka.fxapp.export.VideoResolution;
import org.beryx.viewreka.fxui.Dialogs;
import org.beryx.viewreka.fxui.FXMLNode;
import org.beryx.viewreka.fxui.chart.FxChartBuilder;
import org.beryx.viewreka.fxui.settings.GuiSettings;
import org.beryx.viewreka.parameter.Parameter;

/**
 * A dialog for exporting chart animations as MP4s or animated GIFs.
 */
public class ExportVideo extends GridPane implements FXMLNode {
    public static final String PROP_VIDEO_EXPORT_DIR = "video.export.dir";

    private final GuiSettings guiSettings;
    private final FxChartBuilder<?> chartBuilder;
    private final Parameter<?> iteratedParameter;
    private final long chartFrameDurationMillis;

    @FXML ToggleGroup exportType;
    @FXML RadioButton butAnimatedGif;
    @FXML RadioButton butMP4Video;

    @FXML ComboBox<VideoResolution> cmbResolution;

    @FXML CheckBox ckLoop;
    @FXML Label lbFramesPerSec;
    @FXML ComboBox<Integer> cmbFramesPerSec;

    @FXML TextField txtOutputFile;
    @FXML Button butOutputFile;

    @FXML Button butExport;


    public static ExportVideo createWith(GuiSettings guiSettings, FxChartBuilder<?> chartBuilder, Parameter<?> iteratedParameter, long chartFrameDurationMillis) {
        return new ExportVideo(guiSettings, chartBuilder, iteratedParameter, chartFrameDurationMillis).load();
    }

    private ExportVideo(GuiSettings guiSettings, FxChartBuilder<?> chartBuilder, Parameter<?> iteratedParameter, long chartFrameDurationMillis) {
        this.guiSettings = guiSettings;
        this.chartBuilder = chartBuilder;
        this.iteratedParameter = iteratedParameter;
        this.chartFrameDurationMillis = chartFrameDurationMillis;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkNotNull(exportType, "exportType");
        check("butAnimatedGif", butAnimatedGif);
        check("butMP4Video", butMP4Video);
        check("ckLoop", ckLoop);
        check("cmbResolution", cmbResolution);
        check("lbFramesPerSec", lbFramesPerSec);
        check("cmbFramesPerSec", cmbFramesPerSec);
        check("txtOutputFile", txtOutputFile);
        check("butOutputFile", butOutputFile);
        check("butExport", butExport);

        ckLoop.visibleProperty().bind(butAnimatedGif.selectedProperty());
        lbFramesPerSec.visibleProperty().bind(butMP4Video.selectedProperty());
        cmbFramesPerSec.visibleProperty().bind(butMP4Video.selectedProperty());

        butExport.disableProperty().bind(txtOutputFile.textProperty().isEmpty());

        Rectangle2D scrBounds = Screen.getPrimary().getBounds();
        double scrWidth = scrBounds.getWidth();
        double scrHeight = scrBounds.getHeight();
        for(VideoResolution res : VideoResolution.values()) {
            if(scrWidth < res.getWidth()) continue;
            if(scrHeight < res.getHeight()) continue;
            cmbResolution.getItems().add(res);
        }
        if(cmbResolution.getItems().isEmpty()) {
            Dialogs.error("Export video", "Your screen is too small: " + scrWidth + " x " + scrHeight + ".");
            return;
        }

        SingleSelectionModel<VideoResolution> selModel = cmbResolution.getSelectionModel();
        selModel.select(VideoResolution.R_1280_720);
        if(selModel.getSelectedIndex() < 0) {
            selModel.select(cmbResolution.getItems().size() - 1);
        }

        for(FramesPerSecond fps : FramesPerSecond.values()) {
            cmbFramesPerSec.getItems().add(fps.getFps());
        }
        cmbFramesPerSec.getSelectionModel().select((Integer)FramesPerSecond.FPS_25.getFps());
    }

    public void chooseOutputFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Output file");
        String ext = butAnimatedGif.isSelected() ? "gif" : "mp4";
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(ext.toUpperCase() + " files", "*." + ext),
                new FileChooser.ExtensionFilter("All files", "*.*")
        );

        String initialDirPath = guiSettings.getProperty(PROP_VIDEO_EXPORT_DIR, guiSettings.getMostRecentProjectDir().getAbsolutePath(), false);
        File initialDir = new File(initialDirPath);
        fileChooser.setInitialDirectory(initialDir);

        File outputFile = fileChooser.showSaveDialog(getScene().getWindow());
        if(outputFile != null) {
            txtOutputFile.setText(outputFile.getAbsolutePath());
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void export() {
        String outputFilePath = txtOutputFile.getText();
        if(outputFilePath == null) return;
        File outputFile = new File(outputFilePath);
        File outputDir = outputFile.getParentFile();
        if(outputDir != null) {
            outputDir.mkdirs();
            if(!outputDir.isDirectory()) {
                Dialogs.error("Export video", "Cannot create the output directory '" + outputDir.getAbsolutePath() + "'.");
                return;
            }
            guiSettings.setProperty(PROP_VIDEO_EXPORT_DIR, outputDir.getAbsolutePath());
        }

        VideoResolution resolution = Optional.ofNullable(cmbResolution.getSelectionModel().getSelectedItem()).orElse(VideoResolution.R_1280_720);

        Stage stage = new Stage();
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);

        AnchorPane chartParentPane = new AnchorPane();
        ChartController<?> chartController = chartBuilder.createController(chartParentPane);

        Scene scene = new Scene(chartParentPane);
        stage.setScene(scene);

        stage.setTitle("Export video");

        int width = resolution.getWidth();
        int height = resolution.getHeight();
        chartParentPane.setPrefSize(width, height);
        chartParentPane.setMinSize(width, height);
        chartParentPane.setMaxSize(width, height);

        IntFunction<BufferedImage> imageProvider = i -> {
            BufferedImage[] result = {null};
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                iteratedParameter.setPossibleValue(i);

                Object chartData = chartController.createChartData();
                ((ChartController)chartController).displayChart(chartData);
                chartParentPane.layout();

                WritableImage image = chartParentPane.snapshot(null, null);
                result[0] = SwingFXUtils.fromFXImage(image, null);
                latch.countDown();
            });
            try {
                latch.await();
            } catch(InterruptedException e) {
                Dialogs.error("Export video", "An exception occurred while waiting for the result", e);
            }
            return result[0];
        };


        Object originalValue = iteratedParameter.getValue();
        List<?> possibleValues = iteratedParameter.getPossibleValues();
        int valueCount = possibleValues.size();

        AtomicBoolean cancelFlag = new AtomicBoolean(false);

        stage.setOnShown(ev -> {
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
            stage.setMaxWidth(stage.getWidth());
            stage.setMaxHeight(stage.getHeight());
            stage.setResizable(false);

            Thread exportThread = new Thread(() -> {
                outputFile.delete();
                if(butAnimatedGif.isSelected()) {
                    exportGif(cancelFlag, outputFile, imageProvider, 0, valueCount-1);
                } else {
                    exportMp4(cancelFlag, outputFile, imageProvider, 0, valueCount-1);
                }
                Platform.runLater(() -> {
                    stage.close();
                });
            });
            exportThread.setDaemon(true);
            exportThread.start();
        });

        stage.setOnCloseRequest(ev -> {
            cancelFlag.set(true);
        });

        stage.showAndWait();

        ((Parameter)iteratedParameter).setValue(originalValue);
        ((Stage)getScene().getWindow()).close();
    }

    public void exportGif(AtomicBoolean cancelFlag, File outputFile, IntFunction<BufferedImage> imageProvider, int startPrmIndex, int endPrmIndex) {
        if(endPrmIndex < startPrmIndex) return;

        try(ImageOutputStream output = new FileImageOutputStream(outputFile)) {
            BufferedImage img = imageProvider.apply(startPrmIndex);
            GifSequenceWriter writer = new GifSequenceWriter(output, img.getType(), (int)chartFrameDurationMillis, ckLoop.isSelected());
            writer.writeToSequence(img);
            for(int i = startPrmIndex + 1; i <= endPrmIndex; i++) {
                if(cancelFlag.get()) break;
                img = imageProvider.apply(i);
                writer.writeToSequence(img);
            }
            writer.close();
        } catch(IOException e) {
            Dialogs.error("Export animated GIF", "Cannot export animated GIF", e);
        }
    }

    public void exportMp4(AtomicBoolean cancelFlag, File outputFile, IntFunction<BufferedImage> imageProvider, int startPrmIndex, int endPrmIndex) {
        if(endPrmIndex < startPrmIndex) return;

        Integer fps = cmbFramesPerSec.getSelectionModel().getSelectedItem();
        if(fps == null) {
            fps = FramesPerSecond.FPS_25.getFps();
        }

        int imageCount = endPrmIndex - startPrmIndex + 1;
        try {
            MP4Creator mp4Creator = new MP4Creator();
            mp4Creator.setTimescale(fps);
            mp4Creator.imagesToMP4(cancelFlag, outputFile, i -> imageProvider.apply(startPrmIndex + i), imageCount, chartFrameDurationMillis);
        } catch(IOException e) {
            Dialogs.error("Export video", "Cannot export video", e);
        }
    }

    public void cancelExport() {
        ((Stage)getScene().getWindow()).close();
    }
}
