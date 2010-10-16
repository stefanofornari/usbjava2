/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.ntb.usb;

import junit.framework.TestCase;

/**
 *
 * @author ste
 */
public class UsbInterfaceTest extends TestCase {

    private UsbInterface INTERFACE = null;
    
    public UsbInterfaceTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        INTERFACE = new UsbInterface();

        UsbInterfaceDescriptor[] desc = new UsbInterfaceDescriptor[] {
            new UsbInterfaceDescriptor(),
            new UsbInterfaceDescriptor()
        };

        desc[0].setInterfaceClass((byte)0x01);
        desc[1].setInterfaceClass((byte)0x02);

        INTERFACE.setAlternateSetting(desc);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNullAltSettings() {
        UsbInterface i = new UsbInterface();

        assertNull(i.getAlternateSetting());
        assertEquals(0, i.getNumAlternateSetting());
    }

    public void testGetNumAlternateSetting() {
        assertEquals(2, INTERFACE.getNumAlternateSetting());
    }

    public void testIsNotClass() {
        assertFalse(INTERFACE.isInClass((byte)0x06));
    }
    
    public void testIsClass() {
        assertTrue(INTERFACE.isInClass((byte)0x01));
        assertTrue(INTERFACE.isInClass((byte)0x02));
    }
}
