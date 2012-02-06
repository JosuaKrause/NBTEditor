package nbt.record;

import java.io.IOException;
import java.text.ParseException;

import nbt.write.ByteWriter;

public class NBTNumeric extends NBTRecord {

    private Number payload;

    public NBTNumeric(final NBTType type, final String name,
            final Number payload) {
        super(type, name);
        this.payload = payload;
    }

    public Number getPayload() {
        return payload;
    }

    @Override
    public String getPayloadString() {
        return payload.toString();
    }

    @Override
    public void writePayload(final ByteWriter out) throws IOException {
        switch (tagId) {
        case BYTE:
            out.write(((Byte) payload));
            break;
        case SHORT:
            out.write(((Short) payload));
            break;
        case INT:
            out.write(((Integer) payload));
            break;
        case LONG:
            out.write(((Long) payload));
            break;
        case FLOAT:
            out.write(((Float) payload));
            break;
        case DOUBLE:
            out.write(((Double) payload));
            break;
        default:
            throw new InternalError("missing type? " + tagId);
        }
    }

    @Override
    public boolean isTextEditable() {
        return true;
    }

    @Override
    public String getParseablePayload() {
        return payload.toString();
    }

    @Override
    public void parsePayload(final String str) throws ParseException {
        try {
            switch (tagId) {
            case BYTE:
                payload = Byte.parseByte(str);
                break;
            case SHORT:
                payload = Short.parseShort(str);
                break;
            case INT:
                payload = Integer.parseInt(str);
                break;
            case LONG:
                payload = Long.parseLong(str);
                break;
            case FLOAT:
                payload = Float.parseFloat(str);
                break;
            case DOUBLE:
                payload = Double.parseDouble(str);
                break;
            default:
                throw new InternalError("missing type? " + tagId);
            }
        } catch (final NumberFormatException e) {
            throw new ParseException("invalid number", 0);
        }
        change();
    }

}
