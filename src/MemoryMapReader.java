import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MemoryMapReader implements DataReader {

    private int bufferSize;
    private int nextChunk;
    private int numChunks;
    private int[] record = new int[10];
    private int recordSize = 0;
    private char[] line = new char[30];
    private int lineSize = 0;
    private int val = 0;
    private FileChannel fc;
    private MappedByteBuffer bytes;
    private CharBuffer memory;
    private boolean lastMap = false;

    MemoryMapReader(File file, int maxBufferSize) throws IOException  {
        fc = (new RandomAccessFile(file, "r")).getChannel();
        bufferSize = (fc.size() > (long)(2*maxBufferSize)) ? maxBufferSize : (int)fc.size();
        numChunks = (int)Math.ceil(fc.size()/ bufferSize);
        nextChunk = 0;

    }

    public void seek(int n) throws IOException {
        recordSize = 0;
        lineSize = 0;
        int pos = n % bufferSize;
        if (nextChunk != (n/bufferSize + 1)) {
            nextChunk = n / bufferSize;
            memory.clear();
            readIntoBuffer();

        } else {
            lastMap = (nextChunk >= numChunks);

        }
        memory.position(pos);
    }

    public int position() {
        return (nextChunk - 1) * bufferSize + memory.position();
    }

    public void close() throws IOException {
        fc.close();
    }


    private void readIntoBuffer() throws IOException {
        int mapSize;

        if (lastMap) return;
        if(nextChunk >= numChunks -1) {
            lastMap = true;
            mapSize = (int)fc.size() - (nextChunk * bufferSize);
        } else {
            mapSize = bufferSize;
        }
        bytes = fc.map(FileChannel.MapMode.READ_ONLY, nextChunk * bufferSize,mapSize);
        memory = StandardCharsets.UTF_8.newDecoder().decode(bytes);
        nextChunk++;

    }

    private boolean handleBuffer() throws IOException {
        if (lastMap && memory.position() == memory.limit()) {
            // eof reached and buffer limit reached return null
            return false;
        }
        if (memory == null) {
            // initial map of file
            readIntoBuffer();
        } else if (memory.position() == memory.limit()) {
            // buffer limit reached, map to next file region;
            memory.clear();
            readIntoBuffer();
        }
        return true;
    }

    public char[] readLine() throws IOException {
        char b;
        int to;
        for(;;) {
            if (!handleBuffer()) {
                return null;
            }
            for (int i = memory.position(); i < memory.limit(); i++) {
                b = memory.get(i);
                if (b == '\n') {
                    to = lineSize;
                    lineSize = 0;
                    memory.position(i+1);
                    if (to > 1 && line[to-1] == '\r') {
                        return Arrays.copyOfRange(line, 0, to-1);
                    } else if (to > 0 && line[to-1] != '\r') {
                        return Arrays.copyOfRange(line, 0, to);
                    }
                }
                if (lineSize >= line.length-1) {
                    line = Arrays.copyOf(line, (int) (1.5 * line.length));
                }
                line[lineSize++] = b;
            }
            memory.position(memory.limit());
            if (lineSize > 0 && nextChunk >= numChunks) {
                to = lineSize;
                lineSize = 0;
                if (to > 1 && line[to-1] == '\r') {
                    return Arrays.copyOfRange(line, 0, to-1);
                } else if (to > 0 && line[to-1] != '\r') {
                    return Arrays.copyOfRange(line, 0, to);
                }
            }
        }
    }


    public int[] readRecord(char recordSeparator, char fieldSeparator) throws IOException {
        char b;
        int to;

        for(;;) {
            if (!handleBuffer()) {
                return null;
            }
            for(int i = memory.position(); i<memory.limit(); i++) {
                // get byte at address i
                // if byte == separator
                //      update memory position
                //      return record & reset record
                // else fill record
                b = memory.get(i);
                if (b == recordSeparator) {
                    record[recordSize++] = val;
                    val = 0;
                    to = recordSize;
                    recordSize = 0;
                    memory.position(i+1);
                    if (to > 0)
                        return Arrays.copyOfRange(record,1, to);
                } else if (b == fieldSeparator) {
                    record[recordSize++] = val;
                    val = 0;
                } else {
                    val = 10 * val + Integer.parseInt(String.valueOf(b));
                }
                // resize if record is filled
                if (recordSize >= record.length-1) {
                    record = Arrays.copyOf(record, (int) (1.5 * record.length));
                }
            }
            // if loop exited without returning record
            //      update memory position
            memory.position(memory.limit());
            //
            if (recordSize > 0 && nextChunk >= numChunks) {
                record[recordSize++] = val;
                val = 0;
                to = recordSize;
                recordSize = 0;
                if (to > 0)
                    return Arrays.copyOfRange(record,1, to);
            }
        }
    }
}
