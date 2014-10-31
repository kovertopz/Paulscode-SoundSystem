Paulscode-SoundSystem
=====================

Paulscode's 3D Sound System.


Downloads:

Sound System  Version date:  January 7, 2012  
The core SoundSystem library, independent from 3rd-party libraries.  It is stripped down to the bare essentials, and designed to be easily customizable with various sound library and codec plug-ins.  If memory is a concern (such as in an applet) this may be a good option, because it allows you to choose as many or as few plug-ins as you require for your project.  NOTE: The core SoundSystem library without any plug-ins is only capable of playing MIDI files.  Additional plug-ins should be added for more capabilities.  The source code and license are included in the .zip file.

Sound System jPCT  Version date:  January, 2012  
The jPCT-friendly 3D sound library.  The SoundSystemJPCT class overrides the core SoundSystem libray, and provides a number of methods to make adding 3D sound to any jPCT project easy.  It includes methods for binding Listener to Camera and Sources to Object3Ds, as well as using SimpleVector parameters.  SoundSystemJPCT utilizes the LWJGL binding of OpenAL (with JavaSound as a backup option), and the J-Ogg library for .ogg support.  NOTE: The core SoundSystem library, source code, and all relevant licenses are included in the .zip file.

SoundSystem Utils  Version date:  August 9, 2009  
Includes a SoundSystem loader, and an example XML file.


Plug-ins:

JavaSound library plug-in  Version date:  January 7, 2012  
Interface to the Java Sound API.  More compatible than OpenAL, but not as high quality and fewer features.  This plug-in utilizes JavaSound's panning and volume control methods to simulate an reasonable-quality 3D sound system.  Known bug: quickPlaying sounds will begin playing them at full volume for a split second, before switching to the correct volume.  This is a bug with the Java Sound API itself, and therefore beyond my control to correct.  An easy workaround is to add 0.02 seconds of silence to the beginning of each sound effect (the free Audacity sound editor works well for this).

LWJGL OpenAL library plug-in  Version date:  April 17, 2013  
Interface to the LWJGL binding of OpenAL.  The LWJGL library (http://www.lwjgl.org) is required for this plug-in to work.  This library sounds much better than Java Sound, but is not as compatible.  I recommend using the JavaSound library plug-in as a backup option.  NOTE: Please read the included LWJGL license.

JOAL library plug-in  Version date:  April 17, 2013  
Interface to the JOAL binding of OpenAL.  The JOAL library (http://jogamp.org) is required for this plug-in to work.  As mentioned previously, this library sounds much better than Java Sound, but is not as compatible.  I recommend using the JavaSound library plug-in as a backup option.  NOTE: Please read the included JOAL license.

WAV codec plug-in  Version date:  October 23, 2010  
Adds support for .wav files.

JOgg codec plug-in  Version date:  August 24, 2010  
Adds support for .ogg files using the J-Ogg library.  This codec is less compatible than the JOrbis codec, but the license is less restrictive.  Sometimes running incompatable .ogg files through a converter will make them compatable.  NOTE: Please read the included JOgg license.

JOrbis codec plug-in  Version date:  November 23, 2010  
Adds support for .ogg files using the JOrbis library.  More compatible than the JOgg codec, but reads data more slowly (it may not be possible to stream more than one file simultaneously when using this codec).  This plug-in is licensed by the LGPL.  NOTE: Please read the included LGPL document.

IBXM codec plug-in  Version date:  August 24, 2010  
Adds support for Protracker, Fast Tracker 2, and Scream Tracker 3 (.s2m, .mod, and .xm) files using the IBXM library.  File sizes for these formats tend to be quite small, so this may be a good option for background music.  This plug-in is based on or using the IBXM library, which is bound by the BSD License.  NOTE: Please read the included license document.

JSpeex codec plug-in  Version date:  August 24, 2010  
Adds support for .ogg or .wav files encoded with Speex (a compression optimized for human voice).  See http://www.speex.org/ for more information.


Documentation:

JavaDoc  Version date:  November 8, 2011  
Also includes the JavaDocs for SoundSystemJPCT and all library and codec plug-ins, and the utils library.

3D Sound with SoundSystem  PDF (download the example programs)  
A tutorial-style guide to using the core SoundSystem library (last updated: April 14, 2009).

Guide to SoundSystemJPCT  PDF (download the example programs)  
Another tutorial-style guide to using SoundSystemJPCT. (last updated: April 14, 2009).


Demos:


Sound Effects Player  (download the Source Code)  
Demonstrates library switching on the fly, streaming background music, playing MIDI, and playing multiple sources simultaneously.  Last updated August 21, 2010

Bullet / Target Collision  (download the Source Code)  
Demonstrates the LibraryJavaSound plug-in.  Last updated March 30, 2009

Holy Bouncing Helicopter Balls!  (download the Source Code)  
Demonstrates moving through a world with multiple sources.  Last updated August 21, 2010


What's new?

- Fixed raw data stream repeating bug in LibraryLWJGLOpenAL and LibraryJOAL
- Fixed raw data stream millisecond position bug in LibraryLWJGLOpenAL and LibraryJOAL

« Last Edit: April 18, 2013, 01:44:09 AM by Paul »
