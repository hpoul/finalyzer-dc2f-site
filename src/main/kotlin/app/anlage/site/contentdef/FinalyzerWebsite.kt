package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.render.*
import com.dc2f.richtext.*
import com.dc2f.richtext.markdown.Markdown
import com.dc2f.util.toStringReflective
import com.fasterxml.jackson.annotation.JacksonInject
import mu.KotlinLogging
import org.checkerframework.common.value.qual.IntVal


private val logger = KotlinLogging.logger {}

interface PageSeo : ContentDef {
    var title: String
    val description: String
    val noIndex: Boolean?
}

interface WithMenuDef : ContentDef {
    val menu: MenuDef?
}

interface MenuDef: ContentDef {
    val name: String
}

/** Marker interface for content inside folders. */
interface WebsiteFolderContent : ContentDef, SlugCustomization, WithRedirect, WithMenuDef, WithRenderAlias, WithSitemapInfo {
//    val menu: MenuDef?
//    override val redirect: ContentReference?
    @set:JacksonInject("index")
    var index: WebsiteFolderContent?

    @JvmDefault
    override fun renderAlias(): ContentDef? = index
}

interface WithPageSeo: ContentDef, WithSitemapInfo {
    val seo: PageSeo
}

interface WithMainImage: ContentDef {
    @JvmDefault
    fun mainImage(): ImageAsset? = null
}

interface WithWordCount: ContentDef {
    companion object {
        private val wordCountPattern = Regex("""\b\w[\w\S]*""").toPattern()
    }

    @JvmDefault
    fun wordCount(): Int? = null

    @JvmDefault
    fun countWords(text: String): Int {
        val matcher = wordCountPattern.matcher(text)
        var count = 0
        while (matcher.find()) {
            count++
        }
        return count
    }
}

interface WithAuthor: ContentDef { val author: String }

@Nestable("page")
interface ContentPage : WebsiteFolderContent, WithPageSeo, WithWordCount {
    @set:JacksonInject("body")
    var body: Markdown
    var embed: Embeddables?

    @JvmDefault
    override fun wordCount(): Int? = countWords(body.rawContent)
}

@Nestable("htmlpage")
interface HtmlPage : ContentPage, WithRenderPathOverride {
    /** additional code which will end up in the <head> section of the page. */
    @set:JacksonInject("head")
    var head: RichText?
    @set:JacksonInject("html")
    var html: RichText
    var params: Map<String, Any>?
    var renderOnlyHtml: Boolean?
    var renderPath: String?

    @JvmDefault
    override fun renderPath(renderer: Renderer): RenderPath? =
        renderPath?.let { RenderPath.parseLeafPath(it) }

}

@Nestable("tools")
interface ToolsPage : HtmlPage {
    /** I use one page to take screenshots for the weekly newsletter. */
    val forEmailGeneration: Boolean
}

interface ResizeConfig {
    val width: Int?
    val height: Int?
    val fillType: FillType?
}

interface FigureEmbeddable: ContentDef {
    val alt: String?
    val title: String?
    val image: ImageAsset
    val resize: ResizeConfig?
}

interface Embeddables: ContentDef {
    val references: Map<String, ContentReference>?
    val figures: Map<String, FigureEmbeddable>?
    val files: Map<String, FileAsset>?
    val pebble: Map<String, Pebble>?
}

@Nestable("folder")
interface ContentPageFolder : WebsiteFolderContent, ContentBranchDef<WebsiteFolderContent>

interface Menu : ContentDef {
    val name: String
    val children: List<MenuEntry>
}

interface MenuEntry : ContentDef {
    val name: String?
    val ref: ContentReference?
    val url: String?
}

/*
/
/articles/
/articles/blubb/
 */

fun <T> debugger(id: Any, block: () -> T): T {
    val ret = block()
    logger.debug("got ret: $ret")
    return ret
}

fun List<MenuEntry>.findActiveEntry(loaderContext: LoaderContext, page: ContentDef) =
    debugger(page) { map { it to it.ref?.referencedContent?.let { ref -> loaderContext.subPageDistance(parent = ref, child = page) } } }
        .filter { it.second != null }
        .sortedWith(compareBy { it.second })
        .also {
            logger.debug { "got result $it" }
        }
        .firstOrNull()
        ?.first

@Nestable("partial")
abstract class Partial : ContentDef, Renderable {
    @set:JacksonInject("html")
    abstract var html: RichText

    override fun renderContent(renderContext: RenderContext<*>, arguments: Any?): String =
        html.renderContent(renderContext, arguments)
}


@Nestable("partials")
interface PartialFolder : ContentBranchDef<Partial>, WebsiteFolderContent


val MenuEntry.linkLabel: String
    get() = this.name ?: (this.ref?.referencedContent as? WithPageSeo)?.seo?.title ?: throw Exception("No name for menu entry. ${this.toStringReflective()}")

interface Favicon : ContentDef {
    val image: ImageAsset
}

interface Disqus : ContentDef {
    val shortName: String
}

interface GameConfig : ContentDef {
    val storeUrlAndroid: String
    val storeUrlIOS: String
}

interface FinalyzerConfig : ContentDef {
    val backendUrlProd: String
    val backendUrlDev: String
    val favicons: List<Favicon>
    val signUpUrl: String
    val disqus: Disqus?
    val url: UrlConfig
    val logo: ImageAsset?
    val game: GameConfig
}

abstract class FinalyzerWebsite: Website<WebsiteFolderContent>, WithMenuDef, WithSitemapInfo, WithRenderAlias {
    @set:JacksonInject("index")
    abstract var index: LandingPage
    abstract val mainMenu: List<MenuEntry>
    abstract val footerMenu: List<Menu>
    abstract val config : FinalyzerConfig

    abstract val embed: Embeddables

    abstract val footerContent: ContentReference

//    @JvmDefault
    override fun renderAlias(): ContentDef? = index

}
