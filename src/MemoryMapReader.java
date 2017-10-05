import java.io.*;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class MemoryMapReader implements DataReader {

    static int defaultBufferSize = 2048;
    static int defaultRowSize = 30;
    int size, nChunk, nChunks;
    byte[] row;
    int nRow = 0;
    FileChannel fc;
    MappedByteBuffer memory;

    boolean lastMap = false;

    MemoryMapReader(File file) throws IOException {
        this(file, defaultBufferSize);
    }

    MemoryMapReader(File file, int maxBufferSize) throws IOException  {
        fc = (new RandomAccessFile(file, "r")).getChannel();
        int size = (fc.size() > (long)(2*maxBufferSize)) ? maxBufferSize : (int)fc.size();
        nChunks = (int)Math.ceil(fc.size()/size);
        nChunk = 0;
        row = new byte[defaultRowSize];
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
        if (line[line.length-1] == (byte)'\r') {
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

    private void readIntoBuffer() throws IOException {
        if (lastMap) return;
        int mapSize;
        if(nChunk >= nChunks) {
            lastMap = true;
            mapSize = (int)fc.size()-nChunk*size;
        } else {
            mapSize = size;
        }
        memory = fc.map(FileChannel.MapMode.READ_ONLY,nChunk*size,mapSize);
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

    public byte[] readSeparated(byte sep) throws IOException {
        byte b;
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
                    int to = nRow;
                    nRow = 0;
                    memory.position(i+1);
                    return Arrays.copyOfRange(row,0, to);
                }
                // resize if row is filled
                if (nRow >= row.length-1) {
                    System.out.println("resizing row from " + nRow + " to " + (int) (nRow * 1.5));
                    row = Arrays.copyOf(row, (int) (1.5 * nRow));
                }
                row[nRow++] = b;


            }
            // if loop exited without returning row
            //      update memory position
            memory.position(memory.limit());
            if (lastMap && memory.position() == memory.limit()) {
                return Arrays.copyOfRange(row,0, nRow);
            }
        }
    }
}
