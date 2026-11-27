package io.github.kosmaliczi.songover.media.infrastructure.audio;

public class InvalidAudioFileException extends RuntimeException {

    public InvalidAudioFileException(String message) {
        super(message);
    }
}
