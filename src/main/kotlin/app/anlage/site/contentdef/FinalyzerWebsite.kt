package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.render.RenderContext
import com.dc2f.richtext.RichText
import com.dc2f.richtext.markdown.Markdown
import com.dc2f.util.toStringReflective
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
    var embed: Embeddables?
    var params: Map<String, Any>?
}

interface FigureEmbeddable: ContentDef {
    val alt: String?
    val title: String?
    val image: ImageAsset
}

interface Embeddables: ContentDef {
    val references: Map<String, ContentReference>?
    val figures: Map<String, FigureEmbeddable>?
    val files: Map<String, FileAsset>?
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

@Nestable("partial")
abstract class Partial : ContentDef, Renderable {
    @set:JacksonInject("html")
    abstract var html: RichText

    override fun renderContent(renderContext: RenderContext<*>, arguments: Any?): String =
        html.renderContent(renderContext, arguments)
}


@Nestable("partials")
interface PartialFolder : ContentBranchDef<Partial>, WebsiteFolders


val MenuEntry.linkLabel: String
    get() = this.name ?: (this.ref?.referencedContent as? WithPageSeo)?.seo?.title ?: throw Exception("No name for menu entry. ${this.toStringReflective()}")

interface Favicon : ContentDef {
    val image: ImageAsset
}

interface Disqus : ContentDef {
    val shortName: String
}

interface FinalyzerConfig : ContentDef {
    val backendUrlProd: String
    val backendUrlDev: String
    val favicons: List<Favicon>
    val signUpUrl: String
    val disqus: Disqus?
}

abstract class FinalyzerWebsite: Website<WebsiteFolders> {
    @set:JacksonInject("index")
    abstract var index: LandingPage
    abstract val mainMenu: List<MenuEntry>
    abstract val footerMenu: List<Menu>
    abstract val config : FinalyzerConfig

    abstract val embed: Embeddables

    abstract val footerContent: ContentReference
}
