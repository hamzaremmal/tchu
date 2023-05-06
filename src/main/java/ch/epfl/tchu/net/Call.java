package ch.epfl.tchu.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import ch.epfl.tchu.game.Constants;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Class represents the call in the game.
 * @author Hamza REMMAL (310917)
 * @author Mehdi ZIAZI (311475)
 */
public final class Call {
    
    /**
     * output -> TargetDataLine
     * input  -> SourceDataLine
     */
    
    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;
    private SourceDataLine sourceDataLine;
    private TargetDataLine targetDataLine;
    private AudioFormat audioFormat;
    private AudioInputStream audioInputStream;
    Thread playThread = new PlayThread();
    // Don't record the voice when false
    private BooleanProperty mute = new SimpleBooleanProperty(false);
    // Don't hear the voice when false;
    private BooleanProperty sourdine = new SimpleBooleanProperty(true);

    /**
     * ???
     * @param socket - ???
     * @throws IOException - ???
     */
    public Call(Socket socket) throws IOException {
        this.socket = socket;
        this.input = this.socket.getInputStream();
        this.output = this.socket.getOutputStream();
    }

    /**
     * Start the call by initializing the lines and starting the threads
     */
    public void startCall() {
        audioFormat = getAudioFormat();
        playAudio();
        captureAudio(); 
    }
    
    /**
     * Initialize the object to capture the sound.
     */
    private void captureAudio(){
        try{
            var dataLineInfo = new DataLine.Info(TargetDataLine.class,audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            Thread captureThread = new CaptureThread();
            captureThread.start();
            } catch (Exception e) {
                Platform.runLater(() -> new Alert(AlertType.ERROR, "CAN NOT CONNECT TO THE MIC").show());
                }
        }

    /**
     * Initialize the object to play the received sound.
     */
      private void playAudio() {
        try{
          var dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
          sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
          sourceDataLine.open(audioFormat);
          sourceDataLine.start();
          playThread.start();
        } catch (Exception e) {
            Platform.runLater(() -> new Alert(AlertType.ERROR, "CAN NOT PLAY THE SOUND OF THE CALL").show());
        }
      }

    class CaptureThread extends Thread{
        byte[] tempBuffer = new byte[Constants.BYTES_AUDIO];
        public void run(){
            try{
                while(true){
                    int cnt = targetDataLine.read(tempBuffer,0,tempBuffer.length);
                    if(cnt > 0 && mute.get()){
                        output.write(tempBuffer, 0, cnt);
                        System.out.println("WRITE " + cnt);
                        }
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    this.interrupt();
                    }
            }
        }
    
    
    class PlayThread extends Thread {
      byte[] tempBuffer = new byte[Constants.BYTES_AUDIO];

      public void run(){
        try{
            recreateStream();
          int cnt;
          while(true){
              cnt = audioInputStream.read(tempBuffer, 0,tempBuffer.length);  
            if(cnt > 0 && sourdine.get()){
                System.out.println("READ" + cnt);
                sourceDataLine.write(tempBuffer,0,cnt);
                }
            recreateStream();
            }
          }catch (Exception e) {
              e.printStackTrace();
              this.interrupt();
              }
        }
      }
    
    /**
     * 
     */
    public final void switchMute() { mute.set(!mute.get());}
    
    /**
     * 
     */
    public final void switchSourdine() { sourdine.set(!sourdine.get());}
    
    /**
     * ???
     * @return ???
     */
    public final ReadOnlyBooleanProperty mute() { return mute;}
    
    /**
     * ???
     * @return ???
     */
    public final ReadOnlyBooleanProperty sourdine() { return sourdine;}
    
    
    /**
     * 
     * @throws IOException
     */
    private final void recreateStream() throws IOException {
        audioInputStream = new AudioInputStream(input,audioFormat,input.available()/audioFormat.getFrameSize());
    }
    
    
    /**
     * Creates the AudioFormat
     * @return
     */
    private static AudioFormat getAudioFormat(){
        var sampleRate = 44100f;
        var sampleSizeInBits = 16;
        var channels = 2;
        var signed = true;
        var bigEndian = false;
        return new AudioFormat(sampleRate,sampleSizeInBits,channels,signed,bigEndian);
      }
}
