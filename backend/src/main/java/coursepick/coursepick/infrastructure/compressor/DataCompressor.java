package coursepick.coursepick.infrastructure.compressor;

public interface DataCompressor {

    byte[] compress(String content);

    String decompress(byte[] compressed, int originalSize);
}
