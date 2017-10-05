import java.io.File;
import java.io.IOException;

public class ReaderFactory {
    static int bufferSize = 1024;
    public static ByteBufferReader getByteBufferReader(File file) throws IOException {
        return new ByteBufferReader(file, bufferSize);
    }
    public static ByteBufferReader getByteBufferReader(File file, int bufferSize) throws IOException {
        return new ByteBufferReader(file, bufferSize);
    }
    public static MemoryMapReader getMemoryMapReader(File file) throws IOException {
        return new MemoryMapReader(file, bufferSize);
    }
    public static MemoryMapReader getMemoryMapReader(File file, int bufferSize) throws IOException {
        return new MemoryMapReader(file, bufferSize);
    }
}
