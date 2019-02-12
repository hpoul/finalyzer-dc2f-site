package app.anlage.site.contentdef

import com.dc2f.*
import com.fasterxml.jackson.annotation.JacksonInject
import java.time.ZonedDateTime


@Nestable("blog")
interface Blog: WebsiteFolders, ContentBranchDef<Article> {
    val seo: PageSeo
}

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
