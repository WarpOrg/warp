package net.warpgame.engine.audio.decoder;

import org.apache.commons.io.FilenameUtils;
import net.warpgame.engine.core.context.EngineContext;

import java.io.File;
import java.io.IOException;

/**
 * Created by Marcin on 11.05.2017.
 */
public class SoundDecoderManager {

    public static SoundData decode(String pathToFile) throws IOException {
        if (pathToFile.endsWith("wav")) {

            File soundFile = new File(EngineContext.CODESOURCE_DIR + pathToFile);
            return WavFileDecoder.decode(soundFile);
        }else if(pathToFile.endsWith("ogg")) {
            return OggFileDecoder.decode(EngineContext.CODESOURCE_DIR + pathToFile);
        }else {
            throw new RuntimeException("No support for "
                    + pathToFile.substring(pathToFile.length()-3, pathToFile.length()) + " file system");
        }
    }
}
