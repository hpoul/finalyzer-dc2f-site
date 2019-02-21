package app.anlage.site.templates

import app.anlage.site.FinalyzerTheme
import app.anlage.site.contentdef.*
import com.dc2f.render.RenderContext
import com.dc2f.richtext.*
import com.dc2f.richtext.markdown.Markdown
import com.dc2f.util.toStringReflective
import kotlinx.html.*
import kotlinx.html.stream.appendHTML


fun HTMLTag.richText(context: RenderContext<*>, richText: RichText?, arguments: Any? = null) {
    when (richText) {
        null -> return
        is Markdown -> markdown(context, richText)
        is Mustache -> unsafe { +richText.renderContent(context, arguments) }
        is Pebble -> unsafe { +richText.renderContent(context, arguments) }
        else -> throw Exception("Invalid body ${richText.toStringReflective()}")
    }
}

fun FinalyzerTheme.contentTemplates() {
    config.pageRenderer<HtmlPage> {
        out.appendHTML().baseTemplate(context, headInject = { richText(context, node.head) }) {
            // DIFF because of some reason i have used `div` instead of `section` on old page.
            div("section") {
                div("container") {
                    div("columns is-centered") {
                        div("column has-text-centered is-half is-narrow") {
                            h1("title") { +node.seo.title }
                            div("content") {
                                markdown(context, node.body)
                            }
                        }
                    }

                    richText(context, node.html)

                }
            }
        }
    }
    config.pageRenderer<ContentPage> {
        out.appendHTML().baseTemplate(context) {
            section("section") {
                div("container") {
                    div("content") {
                        markdown(context, node.body)
                    }
                }
            }
        }
    }
    config.pageRenderer<ContentPageFolder> {
        renderChildren(node.children)
    }
}
