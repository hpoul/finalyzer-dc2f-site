package app.anlage.site.templates

import app.anlage.site.FinalyzerTheme
import app.anlage.site.contentdef.*
import com.dc2f.*
import com.dc2f.assets.DigestTransformer
import com.dc2f.render.*
import com.dc2f.richtext.*
import com.dc2f.richtext.markdown.Markdown
import com.dc2f.util.*
import kotlinx.html.*


fun HTMLTag.richText(context: RenderContext<*>, richText: RichText?, arguments: Any? = null) {
    when (richText) {
        null -> return
        is Markdown -> markdown(context, richText)
        is Mustache -> unsafe { +richText.renderContent(context, arguments) }
        is Pebble -> unsafe { +richText.renderContent(context, arguments) }
        else -> throw Exception("Invalid body ${richText.toStringReflective()}")
    }
}

data class BreadcrumbEntry(
    val path: ContentPath,
    val content: ContentDef,
    val href: String
) {
    constructor(context: RenderContext<*>, path: ContentPath, content: ContentDef) : this(path, content, context.href(content))
    constructor(context: RenderContext<*>, path: ContentPath) : this(context, path, requireNotNull(context.renderer.loaderContext.contentByPath[path]))
}

fun MAIN.breadcrumb(context: RenderContext<*>) {
    val loaderContext = context.renderer.loaderContext
    section("breadcrumb-container") {
        div("container") {
            nav("breadcrumb") {
                ol("nav navbar-nav") {
                    generateSequence(BreadcrumbEntry(context, loaderContext.findContentPath(context.node), context.node)) { entry ->
                        (!entry.path.isRoot).then {
                            entry.path.parent().let {
                                BreadcrumbEntry(context, it, loaderContext.contentByPath[it] as ContentDef)
                            }
                        }
                    }.toList().distinctBy { it.href }.reversed().map {
                        findPageTitle(it.content)?.let { title ->
                            li {
                                if (it.content == context.node) {
                                    classes = classes + "is-active"
                                }
                                a(context.href(it.content)) {
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
        appendHTML().baseTemplate(context, headInject = { richText(context, node.head) }) {
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
            script(
                // DIFF don't include type=".."
                // type = ScriptType.textJavaScript,
                src = context.getAsset("theme/script/weekly-stats-component.js")
                    .transform(DigestTransformer())
                    .href(RenderPath.parse("/script/"))
            ) { }


        }
    }

    config.pageRenderer<HtmlPage> {
        appendHTML().baseTemplate(context, headInject = { richText(context, node.head) }) {
            if (node.renderOnlyHtml == true) {
                requireNotNull(node.html) { "renderOnlyHtml was defined true, but no html attribute was found."}
                richText(context, node.html)
            } else {
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
    }
    config.pageRenderer<ContentPage> {
        appendHTML().baseTemplate(context) {
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
            createSubContext(index, out = out).render()
        }
    }
}
