package com.usim.engine.engine.util;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Utils {

    public static String loadResource(String fileName) throws Exception {
        String result;
        try (InputStream in = Utils.class.getResourceAsStream(fileName)) {
            assert in != null;
            try (Scanner scanner = new Scanner(in, java.nio.charset.StandardCharsets.UTF_8.name())) {
                result = scanner.useDelimiter("\\A").next();
            }
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

        buffer.flip();
        return buffer;
    }

    private static @NotNull ByteBuffer resizeBuffer(@NotNull ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }
}
