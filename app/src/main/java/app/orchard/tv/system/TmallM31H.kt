package app.orchard.tv.system

import android.os.Build

/**
 * Alibaba Tmall MagicBox M31H (天猫魔盒): YunOS on Allwinner H313.
 *
 * Not a China Mobile 魔百和. That operator line is a different product (the nearby
 * sku is M301H, usually HiSilicon) and must not take this box's guard paths.
 *
 * The old check was `Build.MODEL.startsWith("MagicBox")`. It was written against
 * this unit, so `ro.product.model` begins with `MagicBox`, but the same prefix is
 * every other Tmall sku and any firmware that reused the word.
 *
 * Strings follow the published MagicBox_M19 build.prop from the same YunOS line
 * (ZNDS, 2019): `ro.product.model`, `ro.product.device`, `ro.product.name` and
 * `ro.product.board` are all `MagicBox_M19`, `ro.product.brand` is `MBX`, and
 * `ro.build.flavor` is `MagicBox_M19-user`. This box's sku is M31H, so:
 *
 *   Build.MODEL  (ro.product.model)  = "MagicBox_M31H"
 *   Build.DEVICE (ro.product.device) = "MagicBox_M31H"
 *
 * Either field is enough; the M19 image sets both to the same `MagicBox_<sku>`.
 * Brand and manufacturer are not required to match. On the M19 the brand is
 * `MBX` while the model is `MagicBox_*`, and Allwinner images often put the SoC
 * vendor in `ro.product.manufacturer`. Requiring a brand that has not been read
 * off this unit would turn the fixes off on the only box they exist for.
 * [Build.BRAND] and [Build.MANUFACTURER] are still read, and only to reject a
 * China Mobile / CMCC marker if a firmware ever reused the M31H model string.
 * Other Tmall skus (`MagicBox_M16` / `M17` / `M18` / `M19`) and a bare
 * `MagicBox` model do not match.
 */
object TmallM31H {
    /** `ro.product.model` on the Tmall MagicBox M31H. */
    const val MODEL = "MagicBox_M31H"

    /** `ro.product.device` on the Tmall MagicBox M31H; same `MagicBox_<sku>` as [MODEL]. */
    const val DEVICE = "MagicBox_M31H"

    fun isTmallM31H(
        model: String = Build.MODEL.orEmpty(),
        device: String = Build.DEVICE.orEmpty(),
        brand: String = Build.BRAND.orEmpty(),
        manufacturer: String = Build.MANUFACTURER.orEmpty(),
    ): Boolean {
        val namesThisBox = model.equals(MODEL, ignoreCase = true) || device.equals(DEVICE, ignoreCase = true)
        if (!namesThisBox) return false
        val vendor = "$brand $manufacturer"
        return CMCC_MARKERS.none { vendor.contains(it, ignoreCase = true) }
    }

    /** Manufacturer / brand fragments that identify a China Mobile 魔百和, not this box. */
    private val CMCC_MARKERS = listOf("CMCC", "CMDC", "ChinaMobile", "China Mobile", "中国移动")
}
