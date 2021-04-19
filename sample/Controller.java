package sample;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

/**
 * Initializable is used when you want to interact with stuff injected with
 * @FXML. During construction those variables aren't filled so you can't interact
 * with them so JavaFX will call initializable after everything is set up.
 */
public class Controller implements Initializable {

    // The main container of the application that holds everything.
    @FXML
    private VBox vboxParent;


    // Media objects used to display and work with the video.
    @FXML
    private MediaView mvVideo;
    private MediaPlayer mpVideo;
    private Media mediaVideo;


    // The HBox that contains all the elements below the video.
    @FXML
    private HBox hBoxControls;
    // HBox that contains the volume label and volume slider.
    @FXML
    private HBox hboxVolume;

    // The button that plays, pauses, and restarts.
    @FXML
    private Button buttonPPR;


    // Labels that are used to display the current and total time.
    @FXML
    private Label labelCurrentTime;
    @FXML
    private Label labelTotalTime;
    // Label that makes the application full screen.
    @FXML
    private Label labelFullScreen;
    // Label that when clicked changes the speed of the application.
    @FXML
    private Label labelSpeed;
    // The label that has the volume icon on it (mute and unmuted).
    @FXML
    private Label labelVolume;


    // The slider used to change the volume.
    @FXML
    private Slider sliderVolume;
    // Slider that lets you control and tracks the current time of the video.
    @FXML
    private Slider sliderTime;

    // Checks if the video is at the end.
    private boolean atEndOfVideo = false;
    // Video is not playing when GUI starts.
    private boolean isPlaying = true;
    // Checks if the video is muted or not.
    private boolean isMuted = true;

    // ImageViews for the buttons and labels.
    private ImageView ivPlay;
    private ImageView ivPause;
    private ImageView ivRestart;
    private ImageView ivVolume;
    private ImageView ivFullScreen;
    private ImageView ivMute;
    private ImageView ivExit;


    // The initialize method is called after all @FXML annotated members have been injected.
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // To create a media player you need to implement the structure of the 3 nested media objects,
        // media, media player, and media view.
        // The media player wraps the media and the media view wraps the media player.
        mediaVideo = new Media(new File("src/resources/hello-world.mp4").toURI().toString());
        mpVideo = new MediaPlayer(mediaVideo);
        mvVideo.setMediaPlayer(mpVideo);

        // Get the paths of the images and make them into images.
        Image imagePlay = new Image(new File("src/resources/play-btn.png").toURI().toString());
        // file:/D:/wittcode-2/java-2/send-video-client/src/resources/play-btn.png
        ivPlay = new ImageView(imagePlay);
        ivPlay.setFitWidth(35);
        ivPlay.setFitHeight(35);

        // Button stop image.
        Image imageStop = new Image(new File("src/resources/stop-btn.png").toURI().toString());
        ivPause = new ImageView(imageStop);
        ivPause.setFitHeight(35);
        ivPause.setFitWidth(35);

        // Restart button image.
        Image imageRestart = new Image(new File("src/resources/restart-btn.png").toURI().toString());
        ivRestart = new ImageView(imageRestart);
        ivRestart.setFitWidth(35);
        ivRestart.setFitHeight(35);

        // Volume (speaker) image.
        Image imageVol = new Image(new File("src/resources/volume.png").toURI().toString());
        ivVolume = new ImageView(imageVol);
        ivVolume.setFitWidth(35);
        ivVolume.setFitHeight(35);

        // Full screen image.
        Image imageFull = new Image(new File("src/resources/fullscreen.png").toURI().toString());
        ivFullScreen = new ImageView(imageFull);
        ivFullScreen.setFitHeight(35);
        ivFullScreen.setFitWidth(35);

        // Muted speaker image.
        Image imageMute = new Image(new File("src/resources/mute.png").toURI().toString());
        ivMute = new ImageView(imageMute);
        ivMute.setFitWidth(35);
        ivMute.setFitHeight(35);

        // Exit full screen image.
        Image imageExit = new Image(new File("src/resources/exitscreen.png").toURI().toString());
        ivExit = new ImageView(imageExit);
        ivExit.setFitHeight(35);
        ivExit.setFitWidth(35);

        /*
         * SET THE DEFAULTS AKA ORIGINALLY STATIC CONTENT
         */

        // When started the button should have the pause sign because it is playing.
        buttonPPR.setGraphic(ivPause);
        // The video starts out muted so originally have the volume label be the muted speaker.
        labelVolume.setGraphic(ivMute);
        // The video is at normal speed at the beginning.
        labelSpeed.setText("1X");
        // The video starts out not in full screen so make the label image the get to full screen one.
        labelFullScreen.setGraphic(ivFullScreen);

        hboxVolume.getChildren().remove(sliderVolume);

        // We want to start out with the slider for the volume removed because we want it to appear
        // When we hover over the volume label.

        // When the play button is clicked handle it.
        buttonPPR.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Get the button that is clicked from the event.
                Button buttonPlay = (Button) actionEvent.getSource();
                bindCurrentTimeLabel();
                // If it is the end of the video then reset the slider to 0 and restart the video.
                if (atEndOfVideo) {
                    sliderTime.setValue(0);
                    atEndOfVideo = false;
                    isPlaying = false;
                }
                // If the video is playing and the button is clicked pause the video and change the image on the button to play.
                if (isPlaying) {
                    buttonPlay.setGraphic(ivPlay);
                    mpVideo.pause();
                    // The video is now paused so change it to false.
                    isPlaying = false;
                } else {
                    // The video was paused so when the button is clicked change the image to stop and play video.
                    buttonPlay.setGraphic(ivPause);
                    mpVideo.play();
                    // The video is now playing so isPlaying is true.
                    isPlaying = true;
                }
            }
        });

        /*
         * SET THE BINDINGS
         */

        // Bind the volume of the video to the volume of the slider.
        // Because this is bindbidirectional it will bind both ways.
        mpVideo.volumeProperty().bindBidirectional(sliderVolume.valueProperty());
        // Bind the value of the slider to the volume of the video.
        sliderVolume.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                // Set the volume of the video to the slider's value.
                mpVideo.setVolume(sliderVolume.getValue());
                // If the video's volume isn't 0 then it is not muted so set the
                // label to the unmuted speaker and set isMuted to false.
                if (mpVideo.getVolume() != 0.0) {
                    labelVolume.setGraphic(ivVolume);
                    isMuted = false;
                } else {
                    // The video is currently muted so set it to the muted speaker
                    // and set isMuted to true.
                    labelVolume.setGraphic(ivMute);
                    isMuted = true;
                }
            }
        });

        // Play the video when the application starts.
        mpVideo.play();

        // When the speed label is clicked on adjust the speed of the video
        // and change the text appropriately.
        labelSpeed.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (labelSpeed.getText().equals("1X")) {
                    labelSpeed.setText("2X");
                    mpVideo.setRate(2.0);
                } else {
                    labelSpeed.setText("1X");
                    mpVideo.setRate(1.0);
                }
            }
        });

        // When the volume label is clicked check if it is already muted. If it
        // is then switch the graphic to the unmuted speaker and set the volume.
        // Note that volume for a media player only works with values between 0.0 and 1.0.
        labelVolume.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // If the video is muted and the volume button was clicked then it
                // should now be unmuted so set the image of the label to the unmuted
                // speaker and set the value of the slider.
                // Then set isMuted to false.
                if (isMuted) {
                    labelVolume.setGraphic(ivVolume);
                    sliderVolume.setValue(0.2);
                    isMuted = false;
                }
                else {
                    // The video is not muted so mute it and change the image.
                    labelVolume.setGraphic(ivMute);
                    sliderVolume.setValue(0);
                    isMuted = true;
                }
            }
        });

        // When the user hovers over the volume label (speaker) find the slider
        // by its ID and if it is null then the slider must have been removed from the scene.
        // In other words, it is null. So if it is null add it to the HBox and
        // set its value to the current volume of the media player (video).
        labelVolume.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (hboxVolume.lookup("#sliderVolume") == null) {
                    hboxVolume.getChildren().add(sliderVolume);
                    sliderVolume.setValue(mpVideo.getVolume());
                }
            }
        });

        hboxVolume.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                hboxVolume.getChildren().remove(sliderVolume);
            }
        });


        // Bind the height of the video player to the height of the scene. The VBox parent
        // is used because it is the parent container so you can get the scene property
        // from it.
        vboxParent.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observableValue, Scene scene, Scene newScene) {
                if (scene == null && newScene != null) {
                    // Match the height of the video to the height of the scene minus the hbox controls height.
                    mvVideo.fitHeightProperty().bind(newScene.heightProperty().subtract(hBoxControls.heightProperty().add(20)));
                }
            }
        });

        // Work with the full screen label.
        labelFullScreen.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                Label label = (Label) mouseEvent.getSource();
                Stage stage = (Stage) label.getScene().getWindow();

                if (stage.isFullScreen()) {
                    stage.setFullScreen(false);
                    labelFullScreen.setGraphic(ivFullScreen);
                } else {
                    stage.setFullScreen(true);
                    labelFullScreen.setGraphic(ivExit);
                    stage.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                        @Override
                        public void handle(KeyEvent keyEvent) {
                            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                                labelFullScreen.setGraphic(ivFullScreen);
                            }
                        }
                    });
                }
            }
        });


        /**
         * totalDurationProperty() - the total amount of play time if allowed to play until finished.
         * This checks how long the the video attached to the media player is.
         * If the media attached to the media player changes then the max of the slider will change as well.
         */
        mpVideo.totalDurationProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldDuration, Duration newDuration) {
                // Note that duraiton is originally in milliseconds.
                // newDuration is the time of the current video, oldDuration is the duration of the previous video.
                sliderTime.setMax(newDuration.toSeconds());
                labelTotalTime.setText(getTime(newDuration));

            }
        });

        // The valueChanging property indicates if the slider is in the process of being changed.
        // When true, indicates the current value of the slider is changing.
        sliderTime.valueChangingProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasChanging, Boolean isChanging) {
                bindCurrentTimeLabel();
                // Once the slider has stopped changing (the user lets go of the slider ball) then set the video to this time.
                if (!isChanging) {
                    // seek() seeks the player to a new time. Note that this has no effect while the player's  status is stopped or the duration is indefinite.
                    mpVideo.seek(Duration.seconds(sliderTime.getValue()));
                }
            }
        });

        // valueChangingProperty() - when true, indicates the current value of the slider is changing.
        // valueProperty() - the current value represented by the slider.

        // ValueProperty() is the current value represented by the slider. This value must always be between min and max.
        sliderTime.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                bindCurrentTimeLabel();
                // Get the current time of the video in seconds.
                double currentTime = mpVideo.getCurrentTime().toSeconds();
                if (Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                    mpVideo.seek(Duration.seconds(newValue.doubleValue()));
                }
                labelsMatchEndVideo(labelCurrentTime.getText(), labelTotalTime.getText());
            }
        });

        mpVideo.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration oldTime, Duration newTime) {
                bindCurrentTimeLabel();
                if (!sliderTime.isValueChanging()) {
                    sliderTime.setValue(newTime.toSeconds());
                }
                labelsMatchEndVideo(labelCurrentTime.getText(), labelTotalTime.getText());
            }
        });

        // What happens at the end of the video.
        mpVideo.setOnEndOfMedia(new Runnable() {
            @Override
            public void run() {
                buttonPPR.setGraphic(ivRestart);
                atEndOfVideo = true;
                // Set the current time to the final time in case it doesn't get rounded up.
                // For example the video could end with 00:39 / 00:40.
                if (!labelCurrentTime.textProperty().equals(labelTotalTime.textProperty())) {
                    labelCurrentTime.textProperty().unbind();
                    labelCurrentTime.setText(getTime(mpVideo.getTotalDuration()) + " / ");
                }
            }
        });


    }

    /**
     * This function takes the time of the video and calculates the seconds, minutes, and hours.
     * @param time - The time of the video.
     * @return Corrected seconds, minutes, and hours.
     */
    public String getTime(Duration time) {

        int hours = (int) time.toHours();
        int minutes = (int) time.toMinutes();
        int seconds = (int) time.toSeconds();

        // Fix the issue with the timer going to 61 and above for seconds, minutes, and hours.
        if (seconds > 59) seconds = seconds % 60;
        if (minutes > 59) minutes = minutes % 60;
        if (hours > 59) hours = hours % 60;

        // Don't show the hours unless the video has been playing for an hour or longer.
        if (hours > 0) return String.format("%d:%02d:%02d",
                hours,
                minutes,
                seconds);
        else return String.format("%02d:%02d",
                minutes,
                seconds);
    }

    // Check the that the text of the time labels match. If they do then we are at the end of the video.
    public void labelsMatchEndVideo(String labelTime, String labelTotalTime) {
        for (int i = 0; i < labelTotalTime.length(); i++) {
            if (labelTime.charAt(i) != labelTotalTime.charAt(i)) {
                atEndOfVideo = false;
                if (isPlaying) buttonPPR.setGraphic(ivPause);
                else buttonPPR.setGraphic(ivPlay);
                break;
            } else {
                atEndOfVideo = true;
                buttonPPR.setGraphic(ivRestart);
            }
        }
    }

    public void bindCurrentTimeLabel() {
        // Bind the text of the current time label to the current time of the video.
        // This will allow the timer to update along with the video.
        labelCurrentTime.textProperty().bind(Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                // Return the hours, minutes, and seconds of the video.
                // %d is an integer
                // Time is given in milliseconds. (For example 750.0 ms).
                return getTime(mpVideo.getCurrentTime()) + " / ";
            }
        }, mpVideo.currentTimeProperty()));
    }
}
