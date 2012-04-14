package nbt.map;

import nbt.read.MapReader.Pair;

public interface ChunkEdit {

  void edit(Chunk c, Pair posInChunk);

}
