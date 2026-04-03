package coursepick.coursepick.infrastructure.compressor;

import com.github.luben.zstd.Zstd;

import java.nio.charset.StandardCharsets;

import static coursepick.coursepick.application.exception.ErrorType.INVALID_COMPRESS_DATA;

public class ZstdCompressor implements DataCompressor {

    private static final int COMPRESSION_LEVEL = 3;

    public byte[] compress(String content) {
        if (content == null || content.isBlank()) {
            throw INVALID_COMPRESS_DATA.create();
        }

        byte[] input = content.getBytes(StandardCharsets.UTF_8);
        return Zstd.compress(input, COMPRESSION_LEVEL);
    }

    public String decompress(byte[] compressed, int originalSize) {
        if (compressed == null || compressed.length == 0) {
            throw INVALID_COMPRESS_DATA.create();
        }

        byte[] decompressed = Zstd.decompress(compressed, originalSize);
        return new String(decompressed, StandardCharsets.UTF_8);
    }
}
