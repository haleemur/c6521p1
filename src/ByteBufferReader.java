import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class ByteBufferReader implements DataReader {
    static int defaultBufferSize = 1024;
    char[] carryOver;
    int nCarryOver = 0;
    FileChannel fc;
    ByteBuffer buffer;
    CharBuffer charBuffer;
    boolean EOFReached = false;

    ByteBufferReader(File file) throws IOException {
        this(file, defaultBufferSize);
    }

    ByteBufferReader(File file, int bufferSize) throws IOException {
        fc = (new FileInputStream(file)).getChannel();
        int size = (fc.size() > (long)(2*bufferSize)) ? bufferSize : (int)fc.size();
        buffer = ByteBuffer.allocate(size);
        charBuffer = buffer.asCharBuffer();
    }

    public void close() throws IOException {
        fc.close();
    }

    public char[] readLine() throws IOException {
        char[] line = readSeparated('\n');
        // handle windows endings;
        if (line[line.length-1] == (int)'\r') {
            return Arrays.copyOfRange(line, 0, line.length-1);
        }
        return line;
    }

    public int[] readRecord(char recordSeparator, char fieldSeparator) throws IOException {
        char[] row = readSeparated(recordSeparator);

        if (row == null) {
            return null;
        }
        String[] rowString = String.valueOf(row).split(String.valueOf(fieldSeparator));
        int[] record = new int[rowString.length-1];
        for (int i=0;i<record.length;i++) {
            record[i] = Integer.parseInt(rowString[i+1]);
        }

        return record;
    }

    private char[] readSeparated(char sep) throws IOException {
        char[] record;
        int from, to;
        if (EOFReached) return null; // return null if the end of file is reached.
        for (;;) {
            // handle memory
            if (charBuffer.limit() == charBuffer.position()) {
                charBuffer.clear();
            }
            if (charBuffer.position() == 0 && charBuffer.limit() > 0) {
                if(fc.read(buffer) <= 0) {
                    EOFReached = true;
                    return carryOver;
                }
                charBuffer.flip();
            }
            // extract record if end delimiter found.
            from = charBuffer.position();
            for(int i=from; i<charBuffer.limit();i++) {
                if (charBuffer.get() == sep) {
                    to = charBuffer.position()-1;
                    if (nCarryOver == 0) {
                        return Arrays.copyOfRange(charBuffer.array(), from, to);
                    }

                    record = new char[nCarryOver + buffer.position()-from-1];
                    // the extra 1 subtracted is for the sep character
                    System.arraycopy(
                            carryOver, 0,
                            record, 0, nCarryOver);
                    System.arraycopy(Arrays.copyOfRange(
                            buffer.array(), from, to), 0,
                            record, nCarryOver, to-from);

                    nCarryOver = 0;
                    carryOver = null;
                    return record;
                }
            }
            // memory limit reached, copy to row
            nCarryOver = buffer.position() - from;
            carryOver = new char[nCarryOver];
            carryOver = Arrays.copyOfRange(charBuffer.array(), from, buffer.position());
        }
    }

    public int position() {
        return -1;
    }
    public void seek(int n) throws IOException {
    }
}
