package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.richtext.markdown.Markdown
import com.dc2f.util.toStringReflective
import com.fasterxml.jackson.annotation.*

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

interface WithPageSeo: ContentDef {
    val seo: PageSeo
}

@Nestable("page")
interface ContentPage : WebsiteFolders, WithPageSeo {
    @set:JacksonInject("body")
    var body: Markdown
}

@Nestable("htmlpage")
interface HtmlPage : ContentPage {
    /** additional code which will end up in the <head> section of the page. */
    @set:JacksonInject("head")
    var head: RichText?
    @set:JacksonInject("html")
    var html: RichText
    var params: Map<String, Any>?
}

@Nestable("folder")
interface ContentPageFolder : WebsiteFolders, ContentBranchDef<WebsiteFolders>

interface Menu : ContentDef {
    val name: String
    val children: List<MenuEntry>
}

interface MenuEntry : ContentDef {
    val name: String?
    val ref: ContentReference?
    val url: String?

}


val MenuEntry.linkLabel: String
    get() = this.name ?: (this.ref?.referencedContent as? WithPageSeo)?.seo?.title ?: throw Exception("No name for menu entry. ${this.toStringReflective()}")

abstract class FinalyzerWebsite: Website<WebsiteFolders> {
    @set:JacksonInject("index")
    abstract var index: LandingPage
    abstract val mainMenu: List<MenuEntry>
    abstract val footerMenu: List<Menu>
}
