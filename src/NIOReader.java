import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class NIOReader {
    int defaultBufferSize = 1024;
    byte[] carryOver;
    int nCarryOver = 0;
    FileChannel fc;
    ByteBuffer buffer = ByteBuffer.allocate(defaultBufferSize);

    boolean EOFReached = false;
    NIOReader(File file) throws IOException {
        fc = (new FileInputStream(file)).getChannel();
    }

    public void close() throws IOException {
        fc.close();
    }
    public byte[] readSeparated(char separator) throws IOException {
        return readSeparated((byte)separator);
    }

    public byte[] readLine() throws IOException {
        byte[] line = readSeparated('\n');
        // handle windows endings;
        if (line[line.length-1] == (int)'\r') {
            return Arrays.copyOfRange(line, 0, line.length-1);
        }
        return line;
    }

    public byte[] readRecord(char delimiter) throws IOException {
        return readRecord((byte)delimiter);
    }

    public byte[] readRecord(byte delimiter) throws IOException {
        int i;
        byte[] row = readSeparated('\t');
        if (row == null) {
            return null;
        } else {
            for(i=0;i<row.length;i++) {
                if (row[i] == delimiter) break;
            }

            byte[] record = new byte[1+(row.length - i - 1)/2];
            int recordSize = 0;
            for (int j=i+1; j< row.length; j+=2) {
                record[recordSize++] = row[j];
            }
            return record;
        }


    }

    public byte[] readSeparated(byte sep) throws IOException {
        byte[] record;
        int from, to;
        if (EOFReached) return null; // return null if the end of file is reached.
        for (;;) {
            // handle buffer
            if (buffer.limit() == buffer.position()) {
                buffer.clear();
            }
            if (buffer.position() == 0 && buffer.limit() > 0) {
                if(fc.read(buffer) <= 0) {
                    EOFReached = true;
                    return carryOver;
                }
                buffer.flip();
            }
            // extract record if end delimiter found.
            from = buffer.position();
            for(int i=from; i<buffer.limit();i++) {
                if (buffer.get() == sep) {
                    to = buffer.position()-1;
                    if (nCarryOver == 0) {
                        return Arrays.copyOfRange(buffer.array(), from, to);
                    }

                    record = new byte[nCarryOver + buffer.position()-from-1];
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
            // buffer limit reached, copy to carryOver
            nCarryOver = buffer.position() - from;
            carryOver = new byte[nCarryOver];
            carryOver = Arrays.copyOfRange(buffer.array(), from, buffer.position());
        }
    }
}
