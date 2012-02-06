package nbt.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import nbt.record.NBTRecord;

public class NBTCellRenderer implements TreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(final JTree tree,
            final Object value, final boolean selected, final boolean expanded,
            final boolean leaf, final int row, final boolean hasFocus) {
        final NBTRecord rec = (NBTRecord) value;
        final String chg = rec.hasChanged() ? "*" : " ";
        final String name = rec.getName();
        if (name == null) {
            return new JLabel(chg
                    + (rec.isTextEditable() ? rec.getParseablePayload() : ""));
        }
        final String type = " (" + rec.getTypeInfo() + ")";
        return new JLabel(
                chg
                        + name
                        + type
                        + (rec.isTextEditable() ? ": "
                                + rec.getParseablePayload() : ""));
    }
}
