package coursepick.coursepick.infrastructure.compressor;

import com.github.luben.zstd.Zstd;

import java.nio.charset.StandardCharsets;

public class ZstdCompressor implements DataCompressor {

    private static final int COMPRESSION_LEVEL = 3;

    public byte[] compress(String content) {
        if (content == null || content.isBlank()) {
            return new byte[0];
        }

        byte[] input = content.getBytes(StandardCharsets.UTF_8);
        return Zstd.compress(input, COMPRESSION_LEVEL);
    }

    public String decompress(byte[] compressed, int originalSize) {
        if (compressed == null || compressed.length == 0) {
            return "";
        }

        byte[] decompressed = Zstd.decompress(compressed, originalSize);
        return new String(decompressed, StandardCharsets.UTF_8);
    }
}
