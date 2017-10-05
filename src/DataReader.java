import java.io.IOException;

interface DataReader {
    void close() throws IOException;
    byte[] readSeparated(char separator) throws IOException;
    byte[] readLine() throws IOException;
    byte[] readRecord(char delimiter) throws IOException;
    byte[] readRecord(byte delimiter) throws IOException;
    byte[] readSeparated(byte sep) throws IOException;
}
