package app.anlage.site

import com.dc2f.*
import com.dc2f.example.SimpleContentFolderChild
import com.fasterxml.jackson.annotation.JacksonInject

interface PageSeo : ContentDef {
    val title: String
    val description: String
}

abstract class FinalyzerWebsite: Website<SimpleContentFolderChild> {
    @set:JacksonInject("index")
    abstract var index: LandingPage
}

@Nestable("landingpage")
interface LandingPage : ContentDef {
    var seo: PageSeo
    @set:JacksonInject("children")
    var children: List<LandingPageElement>
}

sealed class LandingPageElement : ContentDef {
    @Nestable("intro")
    abstract class Intro : LandingPageElement() {
        abstract val teaser: String
        abstract val buttonLabel: String
    }
    @Nestable("hero")
    abstract class Hero : LandingPageElement() {
        abstract val title: String
        @set:JacksonInject("body")
        abstract var body: Markdown
    }
}


