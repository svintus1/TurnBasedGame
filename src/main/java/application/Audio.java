package application;

import javax.sound.sampled.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс Audio предоставляет методы для воспроизведения и управления
 * аудиофайлами. Он использует Java Sound API для обработки воспроизведения
 * звука.
 */
public class Audio {
    private Clip clip;
    private static final Logger logger = LogManager.getLogger(StartController.class.getName());
    /**
     * Воспроизводит указанный аудиофайл.
     *
     * @param fileName Название аудиофайла для воспроизведения, включая формат
     *                 файла.
     */
    public void playMusic(String fileName) {
        String classPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            if (!classPath.endsWith(".jar")) {
                File audioFile = new File("audio/" + fileName);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            } else {
                InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("audio/" + fileName);
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            }
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Останавливает воспроизведение текущего аудио, если оно запущено.
     */
    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Устанавливает уровень громкости воспроизведения аудио.
     *
     * @param volumePercent Уровень громкости в процентах (от 0 до 100).
     */
    public void setVolumePercent(float volumePercent) {
        if (clip != null) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volumeInDecibels = (float) (Math.log10(volumePercent / 100.0) * 20.0);
            gainControl.setValue(volumeInDecibels);
        }
    }
}
