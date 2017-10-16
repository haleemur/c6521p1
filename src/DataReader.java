import java.io.IOException;

interface DataReader {
    void close() throws IOException;
    char[] readLine() throws IOException;
    int[] readRecord(char recordSeparator, char fieldSeparator) throws IOException;
    int position();
    void seek(int n) throws IOException;
}
