
The NBT Editor is an easy to use NBT editor.
The NBT file format is used by minecraft to store
the data of the worlds.

The content of a NBT file is a gzipped stream of
byte data. The following grammar is a complete
description of the format:

    RECORD := END | REAL_RECORD
    
    REAL_RECORD := BYTE | SHORT | INT | LONG | FLOAT
                 | DOUBLE | ARRAY | STRING | LIST | COMPOUND
    
    END := 0
    
    BYTE := 1 NAME RAW_BYTE
    
    SHORT := 2 NAME RAW_SHORT
    
    INT := 3 NAME RAW_INT
    
    LONG := 4 NAME RAW_LONG
    
    FLOAT := 5 NAME RAW_FLOAT
    
    DOUBLE := 6 NAME RAW_DOUBLE
    
    ARRAY := 7 NAME RAW_ARRAY
    
    STRING := 8 NAME RAW_STRING
    
    LIST := 9 NAME RAW_LIST
    
    COMPOUND := 10 NAME RAW_COMPOUND
    
    NAME := RAW_STRING
    
    RAW_BYTE := <signed byte>
    
    RAW_SHORT := <big endian signed short>
    
    RAW_INT := <big endian signed integer>
    
    RAW_LONG := <big endian signed long>
    
    RAW_FLOAT := <big endian IEEE 754-2008 float>
    
    RAW_DOUBLE := <big endian IEEE 754-2008 double>
    
    RAW_ARRAY := RAW_INT <byte array with length given by integer>
    
    RAW_STRING := RAW_SHORT <utf8 string with length given by short>
    
    RAW_LIST := [0-10] RAW_INT ITEM
    // type of item given by byte and length given by integer
    ITEM := RAW_ITEM (ITEM |)
    
    RAW_ITEM := RAW_BYTE | RAW_SHORT | RAW_INT | RAW_LONG | RAW_FLOAT
              | RAW_DOUBLE | RAW_ARRAY | RAW_STRING | RAW_LIST | RAW_COMPOUND
    
    // names within a compound must be unique
    RAW_COMPOUND := REAL_RECORD RAW_COMPOUND | END

The start symbol is RECORD and all numeric symbols stand for
the byte representation.
