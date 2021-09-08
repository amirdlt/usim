package com.usim.engine.engine.util;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) {
        String result;
        try (var in = Utils.class.getResourceAsStream(fileName)) {
            assert in != null;
            try (Scanner scanner = new Scanner(in, StandardCharsets.UTF_8)) {
                result = scanner.useDelimiter("\\A").next();
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("AHD:: IOException during load resource.", e);
        }
        return result;
    }

    public static @NotNull ByteBuffer ioResourceToByteBuffer(String resource, int bufferSize) throws IOException {
        ByteBuffer buffer;

        Path path = Paths.get(resource);
        if (Files.isReadable(path)) {
            try (SeekableByteChannel fc = Files.newByteChannel(path)) {
                buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
                //noinspection StatementWithEmptyBody
                while (fc.read(buffer) != -1)
                    ;
            }
        } else {
            try (InputStream source = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource)) {
                assert source != null;
                try (ReadableByteChannel rbc = Channels.newChannel(source)) {
                    buffer = BufferUtils.createByteBuffer(bufferSize);

                    while (true) {
                        int bytes = rbc.read(buffer);
                        if (bytes == -1)
                            break;
                        if (buffer.remaining() == 0)
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                }
            }
        }

        return buffer.flip();
    }

    private static @NotNull ByteBuffer resizeBuffer(@NotNull ByteBuffer buffer, int newCapacity) {
        return BufferUtils.createByteBuffer(newCapacity).put(buffer.flip());
    }
}
