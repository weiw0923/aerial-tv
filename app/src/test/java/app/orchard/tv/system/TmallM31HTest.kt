package app.orchard.tv.system

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TmallM31HTest {
    @Test fun modelOrDeviceNamesTheTmallM31H() {
        assertTrue(TmallM31H.isTmallM31H(model = "MagicBox_M31H", device = "other"))
        assertTrue(TmallM31H.isTmallM31H(model = "other", device = "MagicBox_M31H"))
        assertTrue(TmallM31H.isTmallM31H(model = "magicbox_m31h", device = "sun50iw12p1", brand = "MBX"))
    }

    @Test fun otherMagicBoxSkusAndTheOperatorBoxDoNotMatch() {
        // The old startsWith("MagicBox") check was true for every one of these.
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox", device = "MagicBox"))
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox_M19", device = "MagicBox_M19", brand = "MBX"))
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox_M16", device = "MagicBox_M16"))
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox_M18", device = "cupid"))
        // China Mobile 魔百和, including one that reused the model string.
        assertFalse(TmallM31H.isTmallM31H(model = "M301H", device = "Hi3798MV310", manufacturer = "CMCC"))
        assertFalse(TmallM31H.isTmallM31H(
            model = "MagicBox_M31H",
            device = "MagicBox_M31H",
            manufacturer = "CMCC",
        ))
        assertFalse(TmallM31H.isTmallM31H(model = "MiTV-MFTR0", device = "malbec", brand = "Xiaomi"))
    }
}
