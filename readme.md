### NBT Editor

The NBT Editor is an easy to use NBT editor.
The NBT file format is used by minecraft to store
the data of the worlds.

The project can be build via ANT (build.xml).
The following jars are generated:

-   *nbt*

    A command line version to generate readable nbt representations.
    In combination with the source package this jar can be
    imported in other projects to use the nbt api.
  
-   *nbt_edit*

    The visual nbt editor.

-   *nbt_map*

    A minecraft anvil map viewer and editor.
    Better run with -Xmx2g or more for larger maps.
    
    NOTE THAT ALL CHANGES TO A MAP IN THIS VIEW ARE PERMANENT!
    
    So you better should backup your world before loading it
    in this editor. Edit the world by choosing a brush and
    right click on the map. The slider can be used to set the radius.

-   *ultrahc.jar*

    Converts a world to a ultra hardcore world. The root folder
    of the world must be provided as an argument. This program
    generates a border for the map and allows to reset the spawn
    to a position outside of the area, position players ready to fight,
    and ensures that the game type for all players is survival.

-   *nbt_src*

    A zip file containing the sources of this project.

### The NBT format

The content of a NBT file is a gzipped stream of
byte data. The following grammar is a complete
description of the format:

    RECORD := END | REAL_RECORD
    
    REAL_RECORD := BYTE | SHORT | INT | LONG | FLOAT | DOUBLE
                 | BYTE_ARRAY | STRING | LIST | COMPOUND | INT_ARRAY
    
    END := 0
    
    BYTE := 1 NAME RAW_BYTE
    
    SHORT := 2 NAME RAW_SHORT
    
    INT := 3 NAME RAW_INT
    
    LONG := 4 NAME RAW_LONG
    
    FLOAT := 5 NAME RAW_FLOAT
    
    DOUBLE := 6 NAME RAW_DOUBLE
    
    BYTE_ARRAY := 7 NAME RAW_BYTE_ARRAY
    
    STRING := 8 NAME RAW_STRING
    
    LIST := 9 NAME RAW_LIST
    
    COMPOUND := 10 NAME RAW_COMPOUND
    
    INT_ARRAY := 11 NAME RAW_INT_ARRAY
    
    NAME := RAW_STRING
    
    RAW_BYTE := <signed byte>
    
    RAW_SHORT := <big endian signed short>
    
    RAW_INT := <big endian signed integer>
    
    RAW_LONG := <big endian signed long>
    
    RAW_FLOAT := <big endian IEEE 754-2008 float>
    
    RAW_DOUBLE := <big endian IEEE 754-2008 double>
    
    RAW_BYTE_ARRAY := RAW_INT <byte array with length given by integer>
    
    RAW_STRING := RAW_SHORT <utf8 string with length given by short>
    
    RAW_LIST := [0-10] RAW_INT ITEM
    // type of item given by byte and length given by integer
    ITEM := RAW_ITEM (ITEM |)
    
    RAW_ITEM := RAW_BYTE | RAW_SHORT | RAW_INT | RAW_LONG | RAW_FLOAT
              | RAW_DOUBLE | RAW_BYTE_ARRAY | RAW_STRING | RAW_LIST | RAW_COMPOUND
    
    // names within a compound must be unique
    RAW_COMPOUND := REAL_RECORD RAW_COMPOUND | END
    
    RAW_INT_ARRAY := RAW_INT <int array with length given by integer>

The start symbol is RECORD and all numeric symbols stand for
the byte representation.

### NBT API

This project also provides an api to manipulate nbt files.
With the classes `nbt.read.NBTReader` and `nbt.write.NBTWriter`
streams or files containing nbt content can be read and written.
The class `nbt.record.NBTRecord` is the main class for nbt content.

Another way of using the api is the class `nbt.world.World`. It
provides an interface to manipulate worlds. For the most things
this works without the explicit use of NBTRecords and provides easy
to use methods to manipulate settings of the world or the maps.
