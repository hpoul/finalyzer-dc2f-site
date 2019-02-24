package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.render.*
import com.dc2f.richtext.Pebble
import com.dc2f.richtext.markdown.Markdown
import com.fasterxml.jackson.annotation.JacksonInject
import java.time.ZonedDateTime


@Nestable("blog")
interface Blog: WebsiteFolderContent, ContentBranchDef<Article>, WithPageSeo


@Nestable("article")
interface Article: ContentDef, SlugCustomization, WithAuthor, WithWordCount, WithMainImage, WebsiteFolderContent, WithRenderPathOverride {
    override var author: String
    val date: ZonedDateTime
    val categories: Array<String>
    val seo: PageSeo
    val title: String
    val headInject: Pebble?
    /**
     * optional sub title, if not defined the date will be shown.
     */
    val subTitle: String?
    var teaser: ImageAsset
    var embed: Embeddables?
    @set:JacksonInject("body")
    var body: Markdown
    /**
     * A bit of a workaround to support a bit more flexible articles where authors can create verbatim
     * html. if this is defined **it is rendered INSTEAD of [body]!!!**
     */
    @set:JacksonInject("html")
    var html: Pebble?
    var mainImageOverride: ImageAsset?
    var renderPath: String?

    @JvmDefault
    override fun slugGenerationValue(): String? = title

    @JvmDefault
    override fun wordCount(): Int? =
        countWords(body.rawContent)

    @JvmDefault
    override fun mainImage(): ImageAsset? = mainImageOverride ?: teaser

    @JvmDefault
    override fun renderPath(renderer: Renderer): RenderPath? =
        renderPath?.let { RenderPath.parseLeafPath(it) }
}

data class LoremIpsum(val blubb: String)
