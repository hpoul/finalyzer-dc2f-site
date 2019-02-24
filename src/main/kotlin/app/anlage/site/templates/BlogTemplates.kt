package app.anlage.site.templates

import app.anlage.site.contentdef.*
import com.dc2f.FillType
import com.dc2f.render.RenderContext
import com.dc2f.richtext.markdown.Markdown
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.time.format.*

fun RenderContext<Blog>.blogIndexPage() {
    appendHTML().baseTemplate(this, node.seo) {
        div("container") {
            node.children
                .sortedByDescending { it.date }
                .map { child ->
                    div("section") {
                        div("columns") {
                            div("column") {
                                a(context.href(child)) {
                                    figure("image is-3by2") {
                                        img("Teaser image") {
                                            style = "max-width: 100%; height: auto;"
                                            src = child.teaser.resize(
                                                context,
                                                480,
                                                320,
                                                FillType.Cover
                                            ).href
                                            width = 480.toString()
                                            height = 320.toString()
                                        }
                                    }
                                }
                            }
                            div("column") {
                                a(context.href(child)) {
                                    h3("title is-size-3") { +child.title }
                                }
                                h4("subtitle is-size-6 is-bold") {
                                    // TODO format date?! make it generic, and cache instance?
                                    +child.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))
//                                    +child.date.toString()
                                }
                                div("content") {
                                    // TODO generate summary?
                                    markdownSummary(context, child.body)
                                }
                                a(context.href(child)) {
                                    i("fas fa-chevron-right") { }
                                    +" Read more"
                                }
                            }
                        }
                    }
                }
        }
    }
}

fun HTMLTag.markdown(context: RenderContext<*>, content: Markdown, asInlineContent: Boolean = false) {
    unsafe { +content.renderedContent(context, asInlineContent = asInlineContent) }
}

fun HTMLTag.markdownSummary(context: RenderContext<*>, content: Markdown) {
    unsafe { +content.summary(context) }
}

fun RenderContext<Article>.blogArticle() {
    appendHTML().baseTemplate(this, node.seo) {
        div("hero is-medium has-bg-img") {
            div("bg-image") {
                // TODO image resize and blur
                style = "background-image: url('${node.teaser.href(context)}')"
                +"x"
            }
            div("hero-body has-text-centered") {
                h1("title") { +node.title }
                h2("subtitle is-size-6 has-text-weight-bold") {
                    // TODO format date
                    +(node.subTitle ?: node.date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)))
                }
            }
        }

        div("container") {
            div("section") {
                div("columns") {
                    div("column is-offset-2 is-8") {
                        div("content has-drop-caps") {
                            node.html?.let { richText(context, node.html) }
                                ?: markdown(context, node.body)
                        }
                    }
                }
            }
        }
    }
}
