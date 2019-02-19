package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.richtext.markdown.Markdown
import com.fasterxml.jackson.annotation.JacksonInject


@Nestable("landingpage")
interface LandingPage : ContentDef {
    /** the pages seo */
    var seo: PageSeo
    @set:JacksonInject("children")
    var children: List<LandingPageElement>
}

interface BackgroundVideo : ContentDef {
    val videoWebm: FileAsset
    val videoMp4: FileAsset
    val placeholder: ImageAsset
}

enum class TextAlign {
    Left,
    Right,
    Center
    ;
}

sealed class LandingPageElement : ContentDef {
    @Nestable("intro")
    abstract class Intro : LandingPageElement() {
        abstract val teaser: String
        abstract val buttonLabel: String
        abstract val backgroundVideo: BackgroundVideo
    }
    @Nestable("hero")
    abstract class Hero : LandingPageElement() {
        abstract val title: String
        @set:JacksonInject("body")
        abstract var body: Markdown
        var bodyTextAlign : TextAlign = TextAlign.Center
        abstract val screenshot: ImageAsset
        abstract val leftAlign: Boolean
    }
    @Nestable("start")
    abstract class Start : LandingPageElement() {
        abstract val title: String
        abstract val subTitle: String
    }
}


