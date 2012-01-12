package client;
// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

import java.io.IOException;

public class PacketConstruction
{

    public void closeStream()
    {
    }

    public void createPacket(int i)
    {
        if(packetStart > (maxPacketLength * 4) / 5)
            try
            {
                writePacket(0);
            }
            catch(IOException ioexception)
            {
                error = true;
                errorText = ioexception.getMessage();
            }
        if(packetData == null)
            packetData = new byte[maxPacketLength];
        packetData[packetStart + 2] = (byte)i;
        packetData[packetStart + 3] = 0;
        packetOffset = packetStart + 3;
        skipOffset = 8;
    }

    public void writePacket(int i)
        throws IOException
    {
        if(error)
        {
            packetStart = 0;
            packetOffset = 3;
            error = false;
            throw new IOException(errorText);
        }
        packetCount++;
        if(packetCount < i)
            return;
        if(packetStart > 0)
        {
            packetCount = 0;
            writeToBuffer(packetData, 0, packetStart);
        }
        packetStart = 0;
        packetOffset = 3;
    }

    public void addByte(int i)
    {
        packetData[packetOffset++] = (byte)i;
    }

    public void addString(String s)
    {
        s.getBytes(0, s.length(), packetData, packetOffset);
        packetOffset += s.length();
    }

    public void addLong(long l)
    {
        addInt((int)(l >> 32));
        addInt((int)(l & -1L));
    }

    public void writeToBuffer(byte abyte0[], int i, int j)
        throws IOException
    {
    }

    public void readInputStream(int i, int j, byte abyte0[])
        throws IOException
    {
    }

    public int readShort()
        throws IOException
    {
        int i = readByte();
        int j = readByte();
        return i * 256 + j;
    }

    public int read()
        throws IOException
    {
        return 0;
    }

    public void read(int i, byte abyte0[])
        throws IOException
    {
        readInputStream(i, 0, abyte0);
    }

    public void addInt(int i)
    {
        packetData[packetOffset++] = (byte)(i >> 24);
        packetData[packetOffset++] = (byte)(i >> 16);
        packetData[packetOffset++] = (byte)(i >> 8);
        packetData[packetOffset++] = (byte)i;
    }

    public void flush()
        throws IOException
    {
        formatPacket();
        writePacket(0);
    }

    public int available()
        throws IOException
    {
        System.out.println("packetconstruction.available WRONG");
        return 0;
    }

    public void addShort(int i)
    {
        packetData[packetOffset++] = (byte)(i >> 8);
        packetData[packetOffset++] = (byte)i;
    }

    public long readLong()
        throws IOException
    {
        long l = readShort();
        long l1 = readShort();
        long l2 = readShort();
        long l3 = readShort();
        return (l << 48) + (l1 << 32) + (l2 << 16) + l3;
    }

    public void formatPacket()
    {
        if(skipOffset != 8)
            packetOffset++;
        int j = packetOffset - packetStart - 2;
        packetData[packetStart] = (byte) (j >> 8);
        packetData[packetStart + 1] = (byte) j;
        if(maxPacketLength <= 10000)
        {
            int k = packetData[packetStart + 2] & 0xff;
            packetCommandCount[k]++;
            packetLengthCount[k] += packetOffset - packetStart;
        }
        packetStart = packetOffset;
    }

    public void addBytes(byte data[], int off, int len)
    {
        for(int i = 0; i < len; i++)
            packetData[packetOffset++] = data[off + i];

    }

    public boolean hasData()
    {
        return packetStart > 0;
    }

    public int readPacket(byte arg0[])
    {
        try
        {
            read++;
            if(maxPacketReadCount > 0 && read > maxPacketReadCount)
            {
                error = true;
                errorText = "time-out";
                maxPacketReadCount += maxPacketReadCount;
                return 0;
            }
            // TODO available = 0
            if(length == 0 && available() >= 2)
            {
                byte buf[] = new byte[2];
                readInputStream(2, 0, buf);
                length = ((short) ((buf[0] & 0xff) << 8) | (short) (buf[1] & 0xff)) + 1;
            }
            if(length > 0 && available() >= length)
            {
                read(length, arg0);
                int i = length;
                length = 0;
                read = 0;
                return i;
            }
        }
        catch(IOException ioexception)
        {
            error = true;
            errorText = ioexception.getMessage();
        }
        return 0;
    }

    public int readByte()
        throws IOException
    {
        return read();
    }

    public PacketConstruction()
    {
        packetOffset = 3;
        skipOffset = 8;
        maxPacketLength = 5000;
        errorText = "";
        error = false;
    }

    protected int length;
    public int read;
    public int maxPacketReadCount;
    public int packetStart;
    private int packetOffset;
    private int skipOffset;
    public byte packetData[];
    final int hbe = 61;
    final int hbf = 59;
    final int hbg = 42;
    final int hbh = 43;
    final int hbi = 44;
    final int hbj = 45;
    final int hbk = 46;
    final int hbl = 47;
    final int hbm = 92;
    final int hbn = 32;
    final int hca = 124;
    final int hcb = 34;
    static char hce[];
    public static int packetCommandCount[] = new int[256];
    protected int maxPacketLength;
    public static int packetLengthCount[] = new int[256];
    protected int packetCount;
    protected String errorText;
    protected boolean error;
    public static int hcl;

    static 
    {
        hce = new char[256];
        for(int i = 0; i < 256; i++)
            hce[i] = (char)i;

        hce[61] = '=';
        hce[59] = ';';
        hce[42] = '*';
        hce[43] = '+';
        hce[44] = ',';
        hce[45] = '-';
        hce[46] = '.';
        hce[47] = '/';
        hce[92] = '\\';
        hce[124] = '|';
        hce[33] = '!';
        hce[34] = '"';
    }
}
