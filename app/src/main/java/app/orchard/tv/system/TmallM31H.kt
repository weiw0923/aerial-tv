package app.orchard.tv.system

import android.os.Build

/**
 * Alibaba Tmall MagicBox M31H (天猫魔盒), Android 10.
 *
 * Not a China Mobile 魔百和. `getprop` on the unit this branch was checked against:
 *
 *   ro.product.model        = MagicBox_M_31_H   (Build.MODEL)
 *   ro.product.device       = MagicBox_M_31_H   (Build.DEVICE)
 *   ro.product.name         = MagicBox_M_31_H   (Build.PRODUCT)
 *   ro.product.brand        = MagicBox_M_31_H   (Build.BRAND)
 *   ro.product.manufacturer = Tmall             (Build.MANUFACTURER)
 *   ro.build.version.release = 10
 *
 * The match is that model or device string, exactly. `MagicBox_M31H` (no underscores
 * around 31) is a different string and does not match. `Build.MODEL.startsWith("MagicBox")`
 * is not used: it is also true for other Tmall skus.
 *
 * [BRAND] and [MANUFACTURER] are the verified Tmall values. After the model or device
 * hits, either one confirms the box: manufacturer `Tmall`, or brand `MagicBox_M_31_H`.
 * There is no denylist. `Tmall` is a match, not a reason to turn the patches off.
 */
object TmallM31H {
    /** `ro.product.model` and `ro.product.device` on the verified unit. */
    const val MODEL = "MagicBox_M_31_H"

    /** Same string as [MODEL]; `ro.product.device` on the verified unit. */
    const val DEVICE = "MagicBox_M_31_H"

    /** `ro.product.brand` on the verified unit. Same string as [MODEL]. */
    const val BRAND = "MagicBox_M_31_H"

    /** `ro.product.manufacturer` on the verified unit. */
    const val MANUFACTURER = "Tmall"

    fun isTmallM31H(
        model: String = Build.MODEL.orEmpty(),
        device: String = Build.DEVICE.orEmpty(),
        brand: String = Build.BRAND.orEmpty(),
        manufacturer: String = Build.MANUFACTURER.orEmpty(),
    ): Boolean {
        val namesThisBox = model == MODEL || device == DEVICE
        if (!namesThisBox) return false
        // Verified vendor: manufacturer Tmall, brand MagicBox_M_31_H. Either is enough
        // once model or device is exact. There is no denylist; "Tmall" is a match.
        return manufacturer == MANUFACTURER || brand == BRAND
    }
}
