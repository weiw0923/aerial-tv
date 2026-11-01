package app.orchard.tv.system

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TmallM31HTest {
    @Test fun verifiedGetpropMatches() {
        assertTrue(TmallM31H.isTmallM31H(
            model = "MagicBox_M_31_H",
            device = "MagicBox_M_31_H",
            brand = "MagicBox_M_31_H",
            manufacturer = "Tmall",
        ))
        assertTrue(TmallM31H.isTmallM31H(model = "MagicBox_M_31_H", device = "other", manufacturer = "Tmall"))
        assertTrue(TmallM31H.isTmallM31H(model = "other", device = "MagicBox_M_31_H", brand = "MagicBox_M_31_H"))
    }

    @Test fun manufacturerTmallDoesNotTurnTheMatchOff() {
        assertTrue(TmallM31H.isTmallM31H(
            model = "MagicBox_M_31_H",
            device = "MagicBox_M_31_H",
            brand = "MagicBox_M_31_H",
            manufacturer = "Tmall",
        ))
    }

    @Test fun otherStringsDoNotMatch() {
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox_M31H", device = "MagicBox_M31H", manufacturer = "Tmall"))
        assertFalse(TmallM31H.isTmallM31H(model = "magicbox_m_31_h", device = "magicbox_m_31_h", manufacturer = "Tmall"))
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox", device = "MagicBox", brand = "MagicBox", manufacturer = "Tmall"))
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox_M19", device = "MagicBox_M19", brand = "MBX"))
        assertFalse(TmallM31H.isTmallM31H(model = "MagicBox_M16", device = "MagicBox_M16"))
        assertFalse(TmallM31H.isTmallM31H(model = "M301H", device = "Hi3798MV310", manufacturer = "CMCC"))
        assertFalse(TmallM31H.isTmallM31H(model = "MiTV-MFTR0", device = "malbec", brand = "Xiaomi", manufacturer = "Xiaomi"))
        assertFalse(TmallM31H.isTmallM31H(model = "other", device = "other", brand = "MagicBox_M_31_H", manufacturer = "Tmall"))
    }
}
