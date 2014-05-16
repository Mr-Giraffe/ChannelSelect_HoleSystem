/**
 * This class is automatically generated by mig. DO NOT EDIT THIS FILE.
 * This class implements a Java interface to the 'CommandMsg'
 * message type.
 */

public class CommandMsg extends net.tinyos.message.Message {

    /** The default size of this message type in bytes. */
    public static final int DEFAULT_MESSAGE_SIZE = 3;

    /** The Active Message type associated with this message. */
    public static final int AM_TYPE = 7;

    /** Create a new CommandMsg of size 3. */
    public CommandMsg() {
        super(DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /** Create a new CommandMsg of the given data_length. */
    public CommandMsg(int data_length) {
        super(data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CommandMsg with the given data_length
     * and base offset.
     */
    public CommandMsg(int data_length, int base_offset) {
        super(data_length, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CommandMsg using the given byte array
     * as backing store.
     */
    public CommandMsg(byte[] data) {
        super(data);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CommandMsg using the given byte array
     * as backing store, with the given base offset.
     */
    public CommandMsg(byte[] data, int base_offset) {
        super(data, base_offset);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CommandMsg using the given byte array
     * as backing store, with the given base offset and data length.
     */
    public CommandMsg(byte[] data, int base_offset, int data_length) {
        super(data, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CommandMsg embedded in the given message
     * at the given base offset.
     */
    public CommandMsg(net.tinyos.message.Message msg, int base_offset) {
        super(msg, base_offset, DEFAULT_MESSAGE_SIZE);
        amTypeSet(AM_TYPE);
    }

    /**
     * Create a new CommandMsg embedded in the given message
     * at the given base offset and length.
     */
    public CommandMsg(net.tinyos.message.Message msg, int base_offset, int data_length) {
        super(msg, base_offset, data_length);
        amTypeSet(AM_TYPE);
    }

    /**
    /* Return a String representation of this message. Includes the
     * message type name and the non-indexed field values.
     */
    public String toString() {
      String s = "Message <CommandMsg> \n";
      try {
        s += "  [nodeId=0x"+Long.toHexString(get_nodeId())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      try {
        s += "  [mode=0x"+Long.toHexString(get_mode())+"]\n";
      } catch (ArrayIndexOutOfBoundsException aioobe) { /* Skip field */ }
      return s;
    }

    // Message-type-specific access methods appear below.

    /////////////////////////////////////////////////////////
    // Accessor methods for field: nodeId
    //   Field type: int, unsigned
    //   Offset (bits): 0
    //   Size (bits): 16
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'nodeId' is signed (false).
     */
    public static boolean isSigned_nodeId() {
        return false;
    }

    /**
     * Return whether the field 'nodeId' is an array (false).
     */
    public static boolean isArray_nodeId() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'nodeId'
     */
    public static int offset_nodeId() {
        return (0 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'nodeId'
     */
    public static int offsetBits_nodeId() {
        return 0;
    }

    /**
     * Return the value (as a int) of the field 'nodeId'
     */
    public int get_nodeId() {
        return (int)getUIntBEElement(offsetBits_nodeId(), 16);
    }

    /**
     * Set the value of the field 'nodeId'
     */
    public void set_nodeId(int value) {
        setUIntBEElement(offsetBits_nodeId(), 16, value);
    }

    /**
     * Return the size, in bytes, of the field 'nodeId'
     */
    public static int size_nodeId() {
        return (16 / 8);
    }

    /**
     * Return the size, in bits, of the field 'nodeId'
     */
    public static int sizeBits_nodeId() {
        return 16;
    }

    /////////////////////////////////////////////////////////
    // Accessor methods for field: mode
    //   Field type: short, unsigned
    //   Offset (bits): 16
    //   Size (bits): 8
    /////////////////////////////////////////////////////////

    /**
     * Return whether the field 'mode' is signed (false).
     */
    public static boolean isSigned_mode() {
        return false;
    }

    /**
     * Return whether the field 'mode' is an array (false).
     */
    public static boolean isArray_mode() {
        return false;
    }

    /**
     * Return the offset (in bytes) of the field 'mode'
     */
    public static int offset_mode() {
        return (16 / 8);
    }

    /**
     * Return the offset (in bits) of the field 'mode'
     */
    public static int offsetBits_mode() {
        return 16;
    }

    /**
     * Return the value (as a short) of the field 'mode'
     */
    public short get_mode() {
        return (short)getUIntBEElement(offsetBits_mode(), 8);
    }

    /**
     * Set the value of the field 'mode'
     */
    public void set_mode(short value) {
        setUIntBEElement(offsetBits_mode(), 8, value);
    }

    /**
     * Return the size, in bytes, of the field 'mode'
     */
    public static int size_mode() {
        return (8 / 8);
    }

    /**
     * Return the size, in bits, of the field 'mode'
     */
    public static int sizeBits_mode() {
        return 8;
    }

}
