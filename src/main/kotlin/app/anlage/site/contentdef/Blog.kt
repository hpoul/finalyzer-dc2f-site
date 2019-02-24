package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.render.*
import com.dc2f.richtext.markdown.Markdown
import com.fasterxml.jackson.annotation.JacksonInject
import java.time.ZonedDateTime


@Nestable("blog")
interface Blog: WebsiteFolderContent, ContentBranchDef<Article>, WithPageSeo


@Nestable("article")
interface Article: ContentDef, SlugCustomization, WithAuthor, WithWordCount, WithMainImage, WebsiteFolderContent {
    override var author: String
    val date: ZonedDateTime
    val categories: Array<String>
    val seo: PageSeo
    val title: String
    /**
     * optional sub title, if not defined the date will be shown.
     */
    val subTitle: String?
    var teaser: ImageAsset
    var embed: Embeddables?
    @set:JacksonInject("body")
    var body: Markdown
    var mainImageOverride: ImageAsset?

    @JvmDefault
    override fun slugGenerationValue(): String? = title

    @JvmDefault
    override fun wordCount(): Int? =
        countWords(body.rawContent)

    @JvmDefault
    override fun mainImage(): ImageAsset? = mainImageOverride ?: teaser
}

data class LoremIpsum(val blubb: String)
