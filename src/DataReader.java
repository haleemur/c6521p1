import java.io.IOException;

interface DataReader {
    void close() throws IOException;
    char[] readSeparated(char separator) throws IOException;
    char[] readLine() throws IOException;
    char[] readRecord(char delimiter) throws IOException;
    int position();
    void seek(int n) throws IOException;
}
