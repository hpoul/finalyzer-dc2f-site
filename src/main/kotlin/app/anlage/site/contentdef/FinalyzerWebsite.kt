package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.render.*
import com.dc2f.richtext.RichText
import com.dc2f.richtext.markdown.Markdown
import com.dc2f.util.toStringReflective
import com.fasterxml.jackson.annotation.JacksonInject
import mu.KotlinLogging


private val logger = KotlinLogging.logger {}

interface PageSeo : ContentDef {
    var title: String
    val description: String
    val noIndex: Boolean?
}

interface MenuDef: ContentDef {
    val name: String
}

/** Marker interface for content inside folders. */
interface WebsiteFolderContent : ContentDef, SlugCustomization, WithRedirect {
    val menu: MenuDef?
//    override val redirect: ContentReference?
}

interface WithPageSeo: ContentDef {
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

    @JvmDefault
    override fun wordCount(): Int? = countWords(body.rawContent)
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

interface FinalyzerConfig : ContentDef {
    val backendUrlProd: String
    val backendUrlDev: String
    val favicons: List<Favicon>
    val signUpUrl: String
    val disqus: Disqus?
    val url: UrlConfig
    val logo: ImageAsset?
}

abstract class FinalyzerWebsite: Website<WebsiteFolderContent> {
    @set:JacksonInject("index")
    abstract var index: LandingPage
    abstract val mainMenu: List<MenuEntry>
    abstract val footerMenu: List<Menu>
    abstract val config : FinalyzerConfig

    abstract val embed: Embeddables

    abstract val footerContent: ContentReference


}
