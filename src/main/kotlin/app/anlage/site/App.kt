/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package app.anlage.site

import app.anlage.site.contentdef.*
import app.anlage.site.templates.*
import com.dc2f.*
import com.dc2f.render.*
import com.dc2f.util.*
import kotlinx.html.*
import mu.KotlinLogging
import org.apache.commons.io.FileUtils
import org.apache.commons.text.StringEscapeUtils
import java.io.File
import java.nio.file.FileSystems

private val logger = KotlinLogging.logger {}

//interface PageSeo : ContentDef {
//    val title: String
//    val description: String
//}
//
//abstract class FinalyzerWebsite: Website<SimpleContentFolderChild> {
//    @set:JacksonInject("index")
//    abstract var index: LandingPage
//}

class FinalyzerTheme : Theme() {
    override fun configure(config: ThemeConfig) {
        config.pageRenderer<FinalyzerWebsite> {
            renderChildren(node.children)
            copyForNode(node.index).render()
        }
        config.pageRenderer<CpcLandingPage> { landingPage() }
        config.pageRenderer<Blog> { renderChildren(node.children); blogIndexPage() }
        config.pageRenderer<Article> { blogArticle() }
        config.pageRenderer<PartialFolder> {  }
        robotsTxt()

        // TODO maybe create a custom variant to register embeddable figures?
        config.pageRenderer<FigureEmbeddable> {
            appendHTML().figure {
//                figure {
                    img {
                        node.resize?.let { resize ->
                            val resized = node.image.resize(context,
                                resize.width ?: Int.MAX_VALUE,
                                resize.height ?: Int.MAX_VALUE,
                                fillType = resize.fillType ?: FillType.Cover)
                            src = resized.href
                            width = resized.width.toString()
                            height = resized.height.toString()
                        } ?: run {
                            src = node.image.href(context)
                        }
                        alt = node.alt ?: node.title ?: ""
//                        width = "200"//child.screenshot.width.toString()
//                        height = "200"//child.screenshot.height.toString()
                    }
                    node.title?.let { title ->
                        figcaption {
                            h4 { +title }
                        }
                    }
//                }

            }
        }
        config.pageRenderer<Disqus> {
            val permalink = enclosingNode?.let { StringEscapeUtils.escapeJson(context.href(it, absoluteUrl = true)) } ?: ""
            // language=html
            out.appendln("""
<div id="disqus_thread"></div>
<script>
    var disqus_config = function () {
    this.page.url = "$permalink";
    };

    (function() {${ /* // DON'T EDIT BELOW THIS LINE */ "" }
        setTimeout(function() {
            var d = document, s = d.createElement('script');
            s.async = true;
            s.src = 'https://${node.shortName}.disqus.com/embed.js';
            s.setAttribute('data-timestamp', +new Date());
            (d.head || d.body).appendChild(s);
        }, 2000);
    })();
</script>
<noscript>Please enable JavaScript to view the <a href="https://disqus.com/?ref_noscript">comments powered by Disqus.</a></noscript>
            """)
        }
        contentTemplates()
//        config.pageRenderer<FinalyzerWebsite>(
//            { ") }
//        )
    }

    override fun renderLinkTitle(content: ContentDef): String? =
        when (content) {
            is WithPageSeo -> content.seo.title
            else -> null
        }
}


fun FinalyzerTheme.robotsTxt() {
    config.pageRenderer<FinalyzerWebsite>(OutputType.robotsTxt) {
        out.appendln("User-agent: *")
        if (Dc2fEnv.current == Dc2fEnv.Production) {
            out.appendln("Allow: /")
            out.appendln("")
            out.appendln("Sitemap: https://anlage.app/sitemap.xml")
        } else {
            out.appendln("Disallow: /")
        }
    }
}


fun main(args: Array<String>) {
    logger.info { "Starting ..." }

//    val gitInfo = GitInfoLoader(FileSystems.getDefault().getPath("web", "content"))
//        .load()
//    val gitInfo = GitInfoLoaderCmd(FileSystems.getDefault().getPath("web", "content"))
//        .load()
//
//    println("gitinfo: $gitInfo")
//    println(Timing.allToString())
//
//    System.exit(0)

    val loadedWebsite = ContentLoader(FinalyzerWebsite::class)
        .load(FileSystems.getDefault().getPath("web", "content")) { loadedWebsite, context ->

            logger.info { "loaded website $loadedWebsite." }
            logger.info {
                "reflected: ${loadedWebsite.toStringReflective()}"
            }

            val targetPath = FileSystems.getDefault().getPath("public")
            Renderer(
                FinalyzerTheme(),
                targetPath,
                loadedWebsite.context,
                urlConfig = loadedWebsite.content.config.url
            ).let { renderer ->
                renderer.renderWebsite(loadedWebsite.content, loadedWebsite.metadata)

                SitemapRenderer(targetPath, loadedWebsite.context, renderer, loadedWebsite.content.config.url)
                    .render()
                AllSitesJsonGenerator(targetPath.resolve("allsites.json"), renderer.loaderContext, renderer)
                    .render()
            }

            // FIXME workaround for now to copy over some assets only referenced by css (fonts)
            FileUtils.copyDirectory(File("web", "static"), targetPath.toFile())

        }

}

