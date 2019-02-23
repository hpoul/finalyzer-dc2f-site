package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.richtext.RichText
import com.dc2f.richtext.markdown.Markdown
import com.fasterxml.jackson.annotation.JacksonInject


interface LandingPage : ContentDef, WebsiteFolderContent {
    /** the pages seo */
    var seo: PageSeo
    @set:JacksonInject(PROPERTY_CHILDREN)
    var children: List<LandingPageElement>
}

@Nestable("landingpage")
abstract class CpcLandingPage : LandingPage, WebsiteFolderContent {
    var couponCode: String = "EARLYADOPTER90"
    var ctaBuyNowLabel: String = "Buy Now!"
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
    @Nestable("cpctry")
    abstract class CpcTry : Start() {
        var offerCoupon = "EARLYADOPTER90"
        var offerTitle = "Limited time offer!"
        var offerSubTitle = Markdown("Early Adopter price, 90% Off! Forever!")
    }
    @Nestable("notready")
    abstract class NotReady : LandingPageElement()

    @Nestable("content")
    abstract class Content  : LandingPageElement() {
        @set:JacksonInject("body")
        abstract var body: RichText
    }
}


