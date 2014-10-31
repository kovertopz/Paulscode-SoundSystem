package paulscode.sound.codecs;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioFormat;

import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;

import org.xiph.speex.OggCrc;
import org.xiph.speex.SpeexDecoder;

/**
 * The CodecJSpeex class provides an ICodec interface for reading from
 * files encoded by Speex.
 *<b><i>   SoundSystem CodecJSpeex Class License:</b></i><br><b><br>
 *    You are free to use this class for any purpose, commercial or otherwise.
 *    You may modify this class or source code, and distribute it any way you
 *    like, provided the following conditions are met:
 *<br>
 *    1) You may not falsely claim to be the author of this class or any
 *    unmodified portion of it.
 *<br>
 *    2) You may not copyright this class or a modified version of it and then
 *    sue me for copyright infringement.
 *<br>
 *    3) If you modify the source code, you must clearly document the changes
 *    made before redistributing the modified source code, so other users know
 *    it is not the original code.
 *<br>
 *    4) You are not required to give me credit for this class in any derived
 *    work, but if you do, you must also mention my website:
 *    http://www.paulscode.com
 *<br>
 *    5) I the author will not be responsible for any damages (physical,
 *    financial, or otherwise) caused by the use if this class or any portion
 *    of it.
 *<br>
 *    6) I the author do not guarantee, warrant, or make any representations,
 *    either expressed or implied, regarding the use of this class or any
 *    portion of it.
 * <br><br>
 *    Author: Paul Lamb
 * <br>
 *    http://www.paulscode.com
 *</b><br><br>
 *<b>
 *    This software is based on or using the JSpeex library available from
 *    http://jspeex.sourceforge.net/index.php
 * <br>
 * <br>
 *    JSpeex is a Java port of the Speex speech codec.  For more information,
 *    visit http://www.speex.org/
 *</b><br><br>
 *<br><b>
 * JSpeex License:
 * <br><br>
 * Copyright (c) 1999-2003 Wimba S.A., All Rights Reserved.
 *<br><br>
 * COPYRIGHT:
 *      This software is the property of Wimba S.A.
 *      This software is redistributed under the Xiph.org variant of
 *      the BSD license.
 *      Redistribution and use in source and binary forms, with or without
 *      modification, are permitted provided that the following conditions
 *      are met:
 *      - Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the following disclaimer.
 *      - Redistributions in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *      - Neither the name of Wimba, the Xiph.org Foundation nor the names of
 *      its contributors may be used to endorse or promote products derived
 *      from this software without specific prior written permission.
 *<br><br>
 * WARRANTIES:
 *      This software is made available by the authors in the hope
 *      that it will be useful, but without any warranty.
 *      Wimba S.A. is not liable for any consequence related to the
 *      use of the provided software.
 *<br><br>
 * Date: 22nd April 2003
 *<br><br>
 *<br>
 *<br>
 * Copyright (C) 2002 Jean-Marc Valin
 *<br><br>
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions
 *  are met:
 *<br><br>
 *  - Redistributions of source code must retain the above copyright
 *  notice, this list of conditions and the following disclaimer.
 *<br><br>
 *  - Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *<br><br>
 *  - Neither the name of the Xiph.org Foundation nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *<br><br>
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE FOUNDATION OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *<br></b>
 */
public class CodecJSpeex implements ICodec
{
/**
 * Used to return a current value from one of the synchronized
 * boolean-interface methods.
 */
    private static final boolean GET = false;

/**
 * Used to set the value in one of the synchronized boolean-interface methods.
 */
    private static final boolean SET = true;

/**
 * Used when a parameter for one of the synchronized boolean-interface methods
 * is not aplicable.
 */
    private static final boolean XXX = false;

/**
 * True if there is no more data to read in.
 */
    private boolean endOfStream = false;

/**
 * True if the stream has finished initializing.
 */
    private boolean initialized = false;

/**
 * Format the converted audio will be in.
 */
    private AudioFormat myAudioFormat = null;

/**
 * Global identifier for when the container speex file is ogg.
 */
    public static final int CONTAINER_FORMAT_OGG  = 1;

/**
 * Global identifier for when the container speex file is wav.
 */
    public static final int CONTAINER_FORMAT_WAV = 2;

/**
* Defines the file format that the speex file is wrapped into.
*/
    private static int sourceFormat  = CONTAINER_FORMAT_OGG;

/**
 * Speex Decoder
 */
    private SpeexDecoder speexDecoder;

/**
 * Whether the speex data is "enhanced"
 */
    private boolean enhanced  = true;
/**
 * Indicates the bandwidth of the encoded audio
 */
    private int mode          = 0;
/**
 * Number of data frames
 */
    private int nframes       = 1;
/**
 * Audio sample-rate
 */
    private int sampleRate    = -1;
/**
 * Audio channels (mono or stereo)
 */
    private int channels      = 1;

/**
 * Input from the URL
 */
    private InputStream is = null;
/**
 * For reading data from 'is'
 */
    private DataInputStream dis = null;


// Header info used by the speex library
//---------------------------------------
    private final int WAV_HEADERSIZE = 8;
    private final short  WAVE_FORMAT_SPEEX = (short) 0xa109;
    private final String RIFF = "RIFF";
    private final String WAVE = "WAVE";
    private final String FORMAT = "fmt ";
    private final String DATA = "data";
    private final int OGG_HEADERSIZE = 27;
    private final int OGG_SEGOFFSET  = 26;
    private final String OGGID = "OggS";
//---------------------------------------

// Buffers for reading in data
//---------------------------------------
    private byte[] header    = new byte[2048];
    private byte[] payload   = new byte[65536];
    private byte[] decdat    = new byte[SoundSystemConfig.getStreamingBufferSize()];
//---------------------------------------

// Varriables used by the speex library for reading
//---------------------------------------
    private int origchksum = 0;
    private int chksum = 0;
    private int curseg = 0;
    private int segments = 0;
    private int packetNo = 0;
    private int bodybytes = 0;
    private int decsize = 0;
//---------------------------------------


/**
 * Processes status messages, warnings, and error messages.
 */
    private SoundSystemLogger logger;

/**
 * Constructor:  Grabs a handle to the logger.
 */
    public CodecJSpeex()
    {
        logger = SoundSystemConfig.getLogger();
    }

/**
 * This method is ignored by CodecJSpeex, because it produces "nice" data.
 * @param b True if the calling audio library requires byte-reversal from certain codecs
 */
    public void reverseByteOrder( boolean b )
    {}

/**
 * Prepares an audio stream to read from.  If another stream is already opened,
 * it will be closed and a new audio stream opened in its place.
 * @param url URL to an audio file to stream from.
 * @return False if an error occurred or if end of stream was reached.
 */
    public boolean initialize( URL url )
    {
        initialized( SET, false );
        cleanup();

        if( url == null )
        {
            errorMessage( "url null in method 'initialize'" );
            cleanup();
            return false;
        }

        try
        {
            is = url.openStream();
            dis =  new DataInputStream( url.openStream() );
        }
        catch( IOException ioe )
        {
            errorMessage( "Unable to open stream in method 'initialize'" );
            printStackTrace( ioe );
            return false;
        }
        speexDecoder = new SpeexDecoder();
        
        if( !processHeader() )
            return false;

        myAudioFormat = new AudioFormat( (float) sampleRate, 16, channels,
                                         true, false );

        endOfStream( SET, false );
        initialized( SET, true );
        return true;
    }

/**
 * Returns false if the stream is busy initializing.
 * @return True if steam is initialized.
 */
    public boolean initialized()
    {
        return initialized( GET, XXX );
    }

/**
 * Reads in one stream buffer worth of audio data.  See
 * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more
 * information about accessing and changing default settings.
 * @return The audio data wrapped into a SoundBuffer context.
 */
    public SoundBuffer read()
    {
        return readBytes( SoundSystemConfig.getStreamingBufferSize() );
    }

/**
 * Reads in all the audio data from the stream (up to the default
 * "maximum file size".  See
 * {@link paulscode.sound.SoundSystemConfig SoundSystemConfig} for more
 * information about accessing and changing default settings.
 * @return the audio data wrapped into a SoundBuffer context.
 */
    public SoundBuffer readAll()
    {
        return readBytes( SoundSystemConfig.getMaxFileSize() );
    }

/**
 * Reads in APPROXIMATELY the specified number of bytes (will be slightly over)
 * @param maxBytes Target size in bytes to read.
 * @return The audio data wrapped into a SoundBuffer context.
 */
    private SoundBuffer readBytes( int maxBytes )
    {
        if( endOfStream( GET, XXX ) )
            return null;

        // Check to make sure there is an audio format:
        if( myAudioFormat == null )
        {
            errorMessage( "Audio Format null in method 'read'" );
            return null;
        }

        byte[] fullBuffer = null;
        int totalBytes = 0;
        try
        {
            if( getSourceFormat() == CONTAINER_FORMAT_OGG )
            {
                for( ; curseg < segments && totalBytes < maxBytes; curseg++ )
                {
                    bodybytes = header[OGG_HEADERSIZE + curseg] & 0xFF;
                    if( bodybytes==255 )
                    {
                        errorMessage( "Unable to handle ogg body size 255 " +
                                      "in method 'readAll'" );
                        return null;
                    }
                    dis.readFully( payload, 0, bodybytes );
                    chksum=OggCrc.checksum( chksum, payload, 0, bodybytes );
                    if( packetNo == 1 )
                    {
                        packetNo++;
                    }
                    else
                    {
                        speexDecoder.processData( payload, 0, bodybytes );
                        for( int i = 1; i < nframes; i++ )
                        {
                            speexDecoder.processData( false );
                        }
                        if( ( decsize = speexDecoder.getProcessedData( decdat,
                                                                     0 ) ) > 0 )
                        {
                            fullBuffer = appendByteArrays( fullBuffer, decdat,
                                                         decsize );
                            totalBytes += decsize;
                        }
                        packetNo++;
                    }
                }
                while( !endOfStream( GET, XXX ) && totalBytes < maxBytes )
                {
                    // read the OGG header
                    dis.readFully( header, 0, OGG_HEADERSIZE );
                    origchksum = readInt( header, 22 );
                    header[22] = 0;
                    header[23] = 0;
                    header[24] = 0;
                    header[25] = 0;
                    chksum=OggCrc.checksum( 0, header, 0, OGG_HEADERSIZE );

                    // make sure its a OGG header
                    if( !OGGID.equals( new String( header, 0, 4 ) ) )
                    {
                        errorMessage( "missing ogg id in method 'readAll'" );
                        return null;
                    }

                    segments = header[OGG_SEGOFFSET] & 0xFF;
                    dis.readFully( header, OGG_HEADERSIZE, segments );
                    chksum=OggCrc.checksum( chksum, header, OGG_HEADERSIZE, segments );

                    for( curseg=0; curseg < segments && totalBytes < maxBytes;
                         curseg++ )
                    {
                        bodybytes = header[OGG_HEADERSIZE + curseg] & 0xFF;
                        if( bodybytes==255 )
                        {
                            errorMessage( "Unable to handle ogg body size " +
                                          "255 in method 'readAll'" );
                            return null;
                        }
                        dis.readFully( payload, 0, bodybytes );
                        chksum=OggCrc.checksum( chksum, payload, 0, bodybytes );

                        speexDecoder.processData( payload, 0, bodybytes );
                        for( int i = 1; i < nframes; i++ )
                        {
                            speexDecoder.processData( false );
                        }
                        if( ( decsize = speexDecoder.getProcessedData( decdat, 0 ) ) > 0 )
                        {
                            fullBuffer = appendByteArrays( fullBuffer, decdat,
                                                         decsize );
                            totalBytes += decsize;
                        }
                        packetNo++;
                    }
                }
            }
            else
            {
                while( !endOfStream( GET, XXX ) && totalBytes < maxBytes )
                {
                    dis.readFully( payload, 0, bodybytes );
                    speexDecoder.processData( payload, 0, bodybytes );
                    for( int i = 1; i < nframes; i++ )
                    {
                        speexDecoder.processData( false );
                    }
                    if( ( decsize = speexDecoder.getProcessedData( decdat, 0 ) )
                                                                           > 0 )
                    {
                        fullBuffer = appendByteArrays( fullBuffer, decdat,
                                                     decsize );
                        totalBytes += decsize;
                    }
                    packetNo++;
                }
            }
        }
        catch( EOFException eof )
        {
            endOfStream( SET, true );
        }
        catch( IOException ioe )
        {
            printStackTrace( ioe );
            return null;
        }

        // Wrap the data into a SoundBuffer:
        SoundBuffer buffer = new SoundBuffer( fullBuffer, myAudioFormat );

        return buffer;
    }

/**
 * Processes the speex header from the container file (ogg or wav).
 * @return False if there was an error.
 */
    private boolean processHeader()
    {
        origchksum = 0;
        chksum = 0;
        curseg = 0;
        segments = 0;
        packetNo = 0;
        bodybytes = 0;
        decsize = 0;

        if( getSourceFormat() == CONTAINER_FORMAT_OGG )
        {
            try
            {
                dis.readFully( header, 0, OGG_HEADERSIZE );
            }
            catch( IOException ioe )
            {
                errorMessage( "Unable to read first segment of ogg header " +
                              "in method 'processHeader'" );
                printStackTrace( ioe );
                return false;
            }

            origchksum = readInt( header, 22 );
            header[22] = 0;
            header[23] = 0;
            header[24] = 0;
            header[25] = 0;
            chksum=OggCrc.checksum( 0, header, 0, OGG_HEADERSIZE );

            // make sure its a OGG header
            if( !OGGID.equals( new String( header, 0, 4 ) ) )
            {
                errorMessage( "Ogg id missing in method 'processHeader'" );
                return false;
            }

            segments = header[OGG_SEGOFFSET] & 0xFF;
            try
            {
                dis.readFully( header, OGG_HEADERSIZE, segments );
            }
            catch( IOException ioe )
            {
                errorMessage( "Unable to read second segment of ogg header " +
                              "in method 'processHeader'" );
                printStackTrace( ioe );
                return false;
            }

            chksum=OggCrc.checksum( chksum, header, OGG_HEADERSIZE, segments );

            for( curseg = 0; packetNo == 0; curseg++ )
            {
                bodybytes = header[OGG_HEADERSIZE + curseg] & 0xFF;
                if( bodybytes == 255 )
                {
                    errorMessage( "Unable to handle ogg body size 255 in " +
                                  "method 'processHeader'" );
                    return false;
                }
                try
                {
                    dis.readFully( payload, 0, bodybytes );
                }
                catch( IOException ioe )
                {
                    errorMessage( "Unable to read segment " + curseg +
                                  " of the ogg body in method " +
                                  "'processHeader'" );
                    printStackTrace( ioe );
                    return false;
                }
                chksum = OggCrc.checksum( chksum, payload, 0, bodybytes );

                if( readSpeexHeader( payload, 0, bodybytes ) )
                {
                    sampleRate = speexDecoder.getSampleRate();
                    channels = speexDecoder.getChannels();
                    packetNo++;
                }
                else
                {
                    packetNo = 0;
                }
            }
        }
        else
        {
            try
            {
                dis.readFully( header, 0, WAV_HEADERSIZE + 4 );
            }
            catch( IOException ioe )
            {
                errorMessage( "Error reading first segment of wav header " +
                              "in method 'processHeader'" );
                printStackTrace( ioe );
                return false;
            }
            if( !RIFF.equals( new String( header, 0, 4 ) ) &&
                !WAVE.equals( new String( header, 8, 4 ) ) )
            {
                errorMessage( "Containing file not in the wav format in " +
                              "method 'processHeader'" );
                return false;
            }
            try
            {
                dis.readFully( header, 0, WAV_HEADERSIZE );
            }
            catch( IOException ioe )
            {
                errorMessage( "Error reading second segment of wav header " +
                              "in method 'processHeader'" );
                printStackTrace( ioe );
                return false;
            }
            String chunk = new String( header, 0, 4 );
            int size = readInt( header, 4 );
            while( !chunk.equals( DATA ) )
            {
                try
                {
                    dis.readFully( header, 0, size );
                }
                catch( IOException ioe )
                {
                    errorMessage( "Error reading segment '" + chunk +
                                  "' of wav header in method " +
                                  "'processHeader'" );
                    printStackTrace( ioe );
                    return false;
                }
                if( chunk.equals( FORMAT ) )
                {
                    if( readShort( header, 0 ) != WAVE_FORMAT_SPEEX )
                    {
                        errorMessage( "File is not a 'Wave Speex' file in " +
                                      "method 'processHeader'" );
                        return false;
                    }
                    channels = readShort( header, 2 );
                    sampleRate = readInt( header, 4 );
                    bodybytes = readShort( header, 12 );
                    if( readShort( header, 16 ) < 82 )
                    {
                        errorMessage( "Possibly corrupt Speex Wave file in " +
                                      "method 'processHeader'" );
                        return false;
                    }
                    readSpeexHeader( header, 20, 80 );
                }
                try
                {
                    dis.readFully( header, 0, WAV_HEADERSIZE );
                }
                catch( IOException ioe )
                {
                    errorMessage( "Error reading title of wav header " +
                                  "segment after segment '" + chunk +
                                  "' in method 'processHeader'" );
                    printStackTrace( ioe );
                    return false;
                }
                chunk = new String( header, 0, 4 );
                size = readInt( header, 4 );
            }
            packetNo++;
        }
        return true;
    }

/**
 * Returns false if there is still more data available to be read in.
 * @return True if end of stream was reached.
 */
    public boolean endOfStream()
    {
        return endOfStream( GET, XXX );
    }

/**
 * Closes the audio stream and remove references to all instantiated objects.
 */
    public void cleanup()
    {
        if( dis != null )
        {
            try
            {
                dis.close();
            }
            catch( IOException ioe )
            {}
            dis = null;
        }
        if( is != null )
        {
            try
            {
                is.close();
            }
            catch( IOException ioe )
            {}
            is = null;
        }
        speexDecoder = null;
    }

/**
 * Returns the audio format of the data being returned by the read() and
 * readAll() methods.
 * @return Information wrapped into an AudioFormat context.
 */
    public AudioFormat getAudioFormat()
    {
        return myAudioFormat;
    }

    /**
    * Returns the version of JSpeex being used.
    * @return String indicating the version.
    */
    public static String version()
    {
        return SpeexDecoder.VERSION;
    }

    /**
    * Reads the header packet.
    * @param packet Packet contents.
    * @param offset The offset from which to start reading.
    * @param bytes The number of bytes to read (should be 80).
    * @return False if there was an error.
     */
    private boolean readSpeexHeader( final byte[] packet, final int offset,
                                     final int bytes )
    {
        if( bytes != 80 )
        {
            errorMessage( "Header byte size not 80 in method " +
                          "'readSpeexHeader'" );
            return false;
        }
        if( !"Speex   ".equals( new String( packet, offset, 8 ) ) )
        {
            return false;
        }
        mode       = packet[40+offset] & 0xFF;
        sampleRate = readInt( packet, offset + 36 );
        channels   = readInt( packet, offset + 48 );
        nframes    = readInt( packet, offset + 64 );
        return speexDecoder.init( mode, sampleRate, channels, enhanced );
    }

    /**
    * Converts Little Endian (Windows) bytes to an int (Java uses Big Endian).
    * @param data the data to read.
    * @param offset the offset from which to start reading.
    * @return the integer value of the reassembled bytes.
    */
    private static int readInt( final byte[] data, final int offset )
    {
        return ( data[offset] & 0xff ) |
               ( ( data[offset + 1] & 0xff ) <<  8 ) |
               ( ( data[offset + 2] & 0xff ) << 16 ) |
               ( data[offset + 3] << 24 );
    }

    /**
    * Converts Little Endian (Windows) bytes to a short (Java uses Big Endian).
    * @param data the data to read.
    * @param offset the offset from which to start reading.
    * @return the integer value of the reassembled bytes.
    */
    private static int readShort( final byte[] data, final int offset )
    {
        return ( data[offset] & 0xff ) |
               ( data[offset + 1] << 8 );
    }

    /**
    * Tells the codec what format of the containing file is in (ogg or wav).
    * @param format Global format identifier.
    */
    public static void setSourceFormat( int format )
    {
        sourceFormat( SET, format );
    }
    /**
    * Indicates the format of the containing file (ogg or wav).
    * @return Global format identifier.
    */
    public static int getSourceFormat()
    {
        return sourceFormat( GET, -1 );
    }
    /**
    * Sets or returns the format of the containing file (ogg or wav).
    * @return Global format identifier.
    * @param action GET or SET.
    * @param format Global format identifier.
    */
    private static synchronized int sourceFormat( boolean action, int format )
    {
        if( action == SET )
            sourceFormat = format;
        return sourceFormat;
    }

/**
 * Internal method for synchronizing access to the boolean 'initialized'.
 * @param action GET or SET.
 * @param value New value if action == SET, or XXX if action == GET.
 * @return True if steam is initialized.
 */
    private synchronized boolean initialized( boolean action, boolean value )
    {
        if( action == SET )
            initialized = value;
        return initialized;
    }

/**
 * Internal method for synchronizing access to the boolean 'endOfStream'.
 * @param action GET or SET.
 * @param value New value if action == SET, or XXX if action == GET.
 * @return True if end of stream was reached.
 */
    private synchronized boolean endOfStream( boolean action, boolean value )
    {
        if( action == SET )
            endOfStream = value;
        return endOfStream;
    }

/**
 * Creates a new array with the second array appended to the end of the first
 * array.
 * @param arrayOne The first array.
 * @param arrayTwo The second array.
 * @param length How many bytes to append from the second array.
 * @return Byte array containing information from both arrays.
 */
    private static byte[] appendByteArrays( byte[] arrayOne, byte[] arrayTwo,
                                            int length )
    {
        byte[] newArray;
        if( arrayOne == null && arrayTwo == null )
        {
            // no data, just return
            return null;
        }
        else if( arrayOne == null )
        {
            // create the new array, same length as arrayTwo:
            newArray = new byte[ length ];
            // fill the new array with the contents of arrayTwo:
            System.arraycopy( arrayTwo, 0, newArray, 0, length );
            arrayTwo = null;
        }
        else if( arrayTwo == null )
        {
            // create the new array, same length as arrayOne:
            newArray = new byte[ arrayOne.length ];
            // fill the new array with the contents of arrayOne:
            System.arraycopy( arrayOne, 0, newArray, 0, arrayOne.length );
            arrayOne = null;
        }
        else
        {
            // create the new array large enough to hold both arrays:
            newArray = new byte[ arrayOne.length + length ];
            System.arraycopy( arrayOne, 0, newArray, 0, arrayOne.length );
            // fill the new array with the contents of both arrays:
            System.arraycopy( arrayTwo, 0, newArray, arrayOne.length,
                              length );
            arrayOne = null;
            arrayTwo = null;
        }

        return newArray;
    }

/**
 * Prints an error message.
 * @param message Message to print.
 */
    private void errorMessage( String message )
    {
        logger.errorMessage( "CodecWav", message, 0 );
    }

/**
 * Prints an exception's error message followed by the stack trace.
 * @param e Exception containing the information to print.
 */
    private void printStackTrace( Exception e )
    {
        logger.printStackTrace( e, 1 );
    }
}
