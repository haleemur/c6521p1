import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MemoryMapReaderInt {

    private static int defaultRecordSize = 30;
    private static int defaultLineSize = 30;
    private int size;
    private int nChunk;
    private int nChunks;
    private int[] record;
    private int nRecord = 0;
    private char[] line;
    private int nLine = 0;
    private int val = 0;
    private FileChannel fc;
    private MappedByteBuffer bytes;
    private CharBuffer memory;
    private boolean lastMap = false;

    MemoryMapReaderInt(File file, int maxBufferSize) throws IOException  {
        fc = (new RandomAccessFile(file, "r")).getChannel();
        size = (fc.size() > (long)(2*maxBufferSize)) ? maxBufferSize : (int)fc.size();
        nChunks = (int)Math.ceil(fc.size()/size);
        nChunk = 0;
        record = new int[defaultRecordSize];
        line = new char[defaultLineSize];
    }

    public void seek(int n) throws IOException {
        lastMap = false;
        nRecord = 0;
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
                    to = nLine;
                    nLine = 0;
                    memory.position(i+1);
                    if (to > 0 && line[to-1] == '\r')
                        return Arrays.copyOfRange(line, 0, to-1);
                    return Arrays.copyOfRange(line, 0, to);
                }
                if (nLine >= line.length-1) {
                    line = Arrays.copyOf(line, (int) (1.5 * line.length));
                }
                line[nLine++] = b;
            }
            memory.position(memory.limit());
            if (nLine > 0 && lastMap) {
                to = nLine;
                nLine = 0;
                if (to > 0 && line[to-1] == '\r')
                    return Arrays.copyOfRange(line, 0, to-1);
                return Arrays.copyOfRange(line, 0, to);
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
                    record[nRecord++] = val;
                    val = 0;
                    to = nRecord;
                    nRecord = 0;
                    memory.position(i+1);
                    if (to > 0)
                        return Arrays.copyOfRange(record,1, to);
                } else if (b == fieldSeparator) {
                    record[nRecord++] = val;
                    val = 0;
                } else {
                    val = 10*val + Integer.parseInt(String.valueOf(b));
                }
                // resize if record is filled
                if (nRecord >= record.length-1) {
                    record = Arrays.copyOf(record, (int) (1.5 * record.length));
                }
            }
            // if loop exited without returning record
            //      update memory position
            memory.position(memory.limit());
            if (nRecord > 0 && lastMap) {
                record[nRecord++] = val;
                val = 0;
                to = nRecord;
                nRecord = 0;
                if (to > 0)
                    return Arrays.copyOfRange(record,1, to);
            }
        }
    }
}
