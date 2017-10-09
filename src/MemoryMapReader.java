import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MemoryMapReader implements DataReader{

    static int defaultBufferSize = 2048;
    static int defaultRowSize = 30;
    int size, nChunk, nChunks;
    char[] row;
    int nRow = 0;
    FileChannel fc;
    MappedByteBuffer bytes;
    CharBuffer memory;

    boolean lastMap = false;

    MemoryMapReader(File file) throws IOException {
        this(file, defaultBufferSize);
    }

    MemoryMapReader(File file, int maxBufferSize) throws IOException  {
        fc = (new RandomAccessFile(file, "r")).getChannel();
        size = (fc.size() > (long)(2*maxBufferSize)) ? maxBufferSize : (int)fc.size();
        nChunks = (int)Math.ceil(fc.size()/size);
        nChunk = 0;
        row = new char[defaultRowSize];
    }

    public void seek(int n) throws IOException {
        lastMap = false;
        nRow = 0;
        nChunk = n / size;
        int pos = n % size;
        memory.clear();
        readIntoBuffer();
        memory.position(pos);
    }

    public int position() {
        return (nChunk-1)*size + memory.position();
    }

    public void close() throws IOException {
        fc.close();
    }

    public char[] readLine() throws IOException {
        char[] line = readSeparated('\n');

        // handle windows endings;
        if (line[line.length-1] == (byte)'\r') {
            return Arrays.copyOfRange(line, 0, line.length-1);
        }
        return line;
    }

    public char[] readRecord(char delimiter) throws IOException {
        int i, j;
        char[] row = readSeparated('\t');
        if (row == null) {
            return null;
        } else {
            for(i=0;i<row.length;i++) {
                if (row[i] == delimiter) break;
            }
            char[] record = new char[1+(row.length - i - 1)/2];
            int recordSize = 0;
            for (j=i+1; j< row.length; j+=2) {
                record[recordSize++] = row[j];
            }
            return record;
        }
    }

    private void readIntoBuffer() throws IOException {
        if (lastMap) return;
        int mapSize;
        if(nChunk >= nChunks) {
            lastMap = true;
            mapSize = (int)fc.size()-nChunk*size;
        } else {
            mapSize = size;
        }
        bytes = fc.map(FileChannel.MapMode.READ_ONLY,nChunk*size,mapSize);
        memory = StandardCharsets.UTF_8.newDecoder().decode(bytes);
        nChunk++;
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

    public char[] readSeparated(char sep) throws IOException {
        char b;
        int to;
        for(;;) {
            for(;;) {
                if (!handleBuffer()) {
                    return null;
                }
                for(int i = memory.position(); i<memory.limit(); i++) {
                    // get byte at address i
                    // if byte == separator
                    //      update memory position
                    //      return row & reset row
                    // else fill row
                    b = memory.get(i);
                    if (b == sep) {
                        to = nRow;
                        nRow = 0;
                        memory.position(i+1);
                        return Arrays.copyOfRange(row,0, to);
                    }
                    // resize if row is filled
                    if (nRow >= row.length-1) {
                        row = Arrays.copyOf(row, (int) (1.5 * nRow));
                    }
                    row[nRow++] = b;


                }
                // if loop exited without returning row
                //      update memory position
                memory.position(memory.limit());
                if (nRow > 0 && lastMap && memory.position() == memory.limit()) {
                    to = nRow;
                    nRow = 0;
                    return Arrays.copyOfRange(row,0, to);
                }
            }
        }
    }
}
