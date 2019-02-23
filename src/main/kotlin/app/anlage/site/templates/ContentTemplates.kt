package app.anlage.site.templates

import app.anlage.site.FinalyzerTheme
import app.anlage.site.contentdef.*
import com.dc2f.*
import com.dc2f.render.RenderContext
import com.dc2f.richtext.*
import com.dc2f.richtext.markdown.Markdown
import com.dc2f.util.*
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

fun MAIN.breadcrumb(context: RenderContext<*>) {
    val loaderContext = context.renderer.loaderContext
    section("breadcrumb-container") {
        div("container") {
            nav("breadcrumb") {
                ol("nav navbar-nav") {
                    generateSequence(loaderContext.findContentPath(context.node) to context.node) {
                        (!it.first.isRoot).then {
                            it.first.parent().let {
                                it to loaderContext.contentByPath[it] as ContentDef
                            }
                        }
                    }.toList().reversed().map {
                        findPageTitle(it.second)?.let { title ->
                            li {
                                if (it.second == context.node) {
                                    classes = classes + "is-active"
                                }
                                a(context.href(it.second)) {
                                    +title
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun findPageTitle(page: ContentDef?): String? =
    (page as? WithRedirect)?.let { findPageTitle(it.redirect?.referencedContent) }
        ?: (page as? WithMenuDef)?.menu?.name
        ?: (page as? WithPageSeo)?.seo?.title

fun FinalyzerTheme.contentTemplates() {

    config.pageRenderer<ToolsPage> {
        // DIFF the only reason to have this separate from [HtmlPage] is for backward compatibility, we can probably easily combine them.
        out.appendHTML().baseTemplate(context, headInject = { richText(context, node.head) }) {
            // TODO breadcrumbs
            breadcrumb(context)

            if (node.forEmailGeneration) {
                div("resize-note") {
                    +"Resize to 900px width!"
                }
            }

            section("section weekly-reports") {
                div("container") {
                    markdown(context, node.body)
                    richText(context, node.html)
                }
            }

            div("report-footer") {
                +"Get your free weekly reports directly into your inbox https://anlage.app/reports"
            }


        }
    }

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
//        node.index?.let(::renderNode)
        node.index?.let { index ->
            copyForNode(index).renderToHtml()
        }
    }
}
