package com.jordanbunke.stipple_effect.utility;

import java.awt.*;
import java.net.URI;

public class WebUtils {
    private static final String
            ITCH_LINK = "https://flinkerflitzer.itch.io/stipple-effect",
            SPONSOR_LINK = "https://github.com/sponsors/jbunke",
            DOCS_LINK = "https://stipple-effect.github.io/docs",
            API_SPEC_LINK = "https://stipple-effect.github.io/api",
            BUG_LINK = "https://github.com/stipple-effect/stipple-effect/issues/new/choose",
            VS_CODE_EXT_LINK = "https://marketplace.visualstudio.com/items?itemName=jordanbunke.deltascript-for-stipple-effect";

    public static void reportBug() {
        visitSite(BUG_LINK);
    }

    public static void storePage() {
        visitSite(ITCH_LINK);
    }

    public static void sponsorPage() {
        visitSite(SPONSOR_LINK);
    }

    public static void documentation() {
        visitSite(DOCS_LINK);
    }

    public static void apiSpec() {
        visitSite(API_SPEC_LINK);
    }

    public static void vsCodeExt() {
        visitSite(VS_CODE_EXT_LINK);
    }

    private static void visitSite(final String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (Exception e) {
            StatusUpdates.invalidLink(link);
        }
    }
}
