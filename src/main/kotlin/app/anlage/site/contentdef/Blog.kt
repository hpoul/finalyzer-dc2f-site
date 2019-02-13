package app.anlage.site.contentdef

import com.dc2f.*
import com.dc2f.richtext.markdown.Markdown
import com.fasterxml.jackson.annotation.JacksonInject
import java.time.ZonedDateTime


@Nestable("blog")
interface Blog: WebsiteFolders, ContentBranchDef<Article>, WithPageSeo

@Nestable("article")
interface Article: ContentDef {
    var author: String
    val date: ZonedDateTime
    val categories: Array<String>
    val seo: PageSeo
    val title: String
    var teaser: ImageAsset
    @set:JacksonInject("body")
    var body: Markdown
}
