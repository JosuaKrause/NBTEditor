package nbt;

import java.io.File;
import java.io.IOException;

import nbt.gui.NBTFrame;
import nbt.read.NBTReader;
import nbt.record.NBTRecord;
import nbt.write.NBTWriter;

public class Main {

    public static void main(final String[] args) throws IOException {
        new NBTFrame().setVisible(true);
    }

    public static final void check() throws IOException {
        final NBTReader read = new NBTReader(new File("./level.dat"));
        final NBTRecord r = read.read();
        read.close();
        System.out.println(new IndentString().indent(r.toString()));
        final NBTWriter write = new NBTWriter(new File("./level0.dat"));
        write.write(r);
        write.close();
        final NBTReader read0 = new NBTReader(new File("./level0.dat"));
        final NBTRecord r0 = read0.read();
        read0.close();
        System.out.println("next");
        System.out.println(new IndentString().indent(r0.toString()));
    }

}
