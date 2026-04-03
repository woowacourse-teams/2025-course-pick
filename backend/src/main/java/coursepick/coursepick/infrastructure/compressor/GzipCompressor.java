package coursepick.coursepick.infrastructure.compressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static coursepick.coursepick.application.exception.ErrorType.*;

public class GzipCompressor implements DataCompressor {

    public byte[] compress(String content) {
        if (content == null || content.isBlank()) {
            throw INVALID_COORDINATE_COUNT.create();
        }

        byte[] input = content.getBytes(StandardCharsets.UTF_8);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(input);
            gzipOutputStream.finish();
        } catch (IOException e) {
            throw COMPRESS_FAIL.create(e.getMessage());
        }

        return byteArrayOutputStream.toByteArray();
    }

    public String decompress(byte[] bytes, int originalSize) {
        if (bytes == null || bytes.length == 0) {
            throw INVALID_COORDINATE_COUNT.create();
        }

        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream(originalSize)) {

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            return outputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw DECOMPRESS_FAIL.create(e.getMessage());
        }
    }
}
