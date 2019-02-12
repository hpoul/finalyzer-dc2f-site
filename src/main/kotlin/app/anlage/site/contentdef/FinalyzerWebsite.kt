package app.anlage.site.contentdef

import com.dc2f.*
import com.fasterxml.jackson.annotation.JacksonInject

interface PageSeo : ContentDef {
    var title: String
    val description: String
}

interface MenuDef: ContentDef {
    val name: String
}

interface WebsiteFolders : ContentDef {
    val menu: MenuDef?
}

abstract class FinalyzerWebsite: Website<WebsiteFolders> {
    @set:JacksonInject("index")
    abstract var index: LandingPage
}
