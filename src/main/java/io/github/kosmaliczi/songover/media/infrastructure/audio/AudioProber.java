package io.github.kosmaliczi.songover.media.infrastructure.audio;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AudioProber {

    private final FfprobeProperties properties;
    private final JsonMapper jsonMapper;

    public AudioMetadata probe(String source) {
        FfprobeOutput output = jsonMapper.readValue(run(source), FfprobeOutput.class);

        FfprobeOutput.Stream audioStream = findAudioStream(output);
        int durationSeconds = parseDuration(output.format());

        return new AudioMetadata(output.format().formatName(), audioStream.codecName(), durationSeconds);
    }

    private String run(String source) {
        List<String> command = List.of(
                properties.path(), "-v", "error",
                "-print_format", "json", "-show_format", "-show_streams",
                source);

        Process process;
        try {
            process = new ProcessBuilder(command).start();
        } catch (IOException ex) {
            throw new IllegalStateException("Could not start ffprobe at '" + properties.path() + "'", ex);
        }

        CompletableFuture<String> stdout = readAsync(process.getInputStream());
        CompletableFuture<String> stderr = readAsync(process.getErrorStream());

        try {
            if (!process.waitFor(properties.timeout().toMillis(), TimeUnit.MILLISECONDS)) {
                process.destroyForcibly();
                throw new IllegalStateException("ffprobe timed out after " + properties.timeout());
            }
        } catch (InterruptedException ex) {
            process.destroyForcibly();
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for ffprobe", ex);
        }

        if (process.exitValue() != 0) {
            String error = stderr.join().replace(source, "<file>").strip();
            throw new InvalidAudioFileException("ffprobe could not read the file: " + error);
        }
        return stdout.join();
    }

    private FfprobeOutput.Stream findAudioStream(FfprobeOutput output) {
        if (output.streams() == null) {
            throw new InvalidAudioFileException("File has no audio stream");
        }
        return output.streams().stream()
                .filter(stream -> "audio".equals(stream.codecType()))
                .findFirst()
                .orElseThrow(() -> new InvalidAudioFileException("File has no audio stream"));
    }

    private int parseDuration(FfprobeOutput.Format format) {
        if (format == null || format.duration() == null) {
            throw new InvalidAudioFileException("Unknown audio duration");
        }
        long seconds;
        try {
            seconds = Math.round(Double.parseDouble(format.duration()));
        } catch (NumberFormatException ex) {
            throw new InvalidAudioFileException("Unknown audio duration");
        }
        if (seconds <= 0) {
            throw new InvalidAudioFileException("Audio is shorter than one second");
        }
        return Math.toIntExact(seconds);
    }

    private CompletableFuture<String> readAsync(InputStream stream) {
        return CompletableFuture.supplyAsync(() -> {
            try (stream) {
                return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        });
    }
}
