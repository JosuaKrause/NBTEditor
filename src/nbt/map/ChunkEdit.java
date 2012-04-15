package nbt.map;


/**
 * A chunk editor.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public interface ChunkEdit {

  /**
   * Edits a position within the chunk.
   * 
   * @param c The chunk.
   * @param posInChunk The position in the chunk.
   */
  void edit(Chunk c, Pair posInChunk);

}
