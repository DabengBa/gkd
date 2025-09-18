package android.content.pm;

import dev.rikka.tools.refine.RefineAs;

/**
 * @noinspection unused
 */
@RefineAs(PackageInfo.class)
public class PackageInfoHidden {

    public String overlayTarget;

    // android9+
    public boolean isOverlayPackage() {
        throw new RuntimeException("Stub");
    }
}