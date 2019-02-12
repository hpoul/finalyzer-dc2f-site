package app.anlage.site.templates

import app.anlage.site.contentdef.*
import com.dc2f.*
import com.dc2f.render.RenderContext
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

fun RenderContext<Blog>.blogIndexPage() {
    out.appendHTML().baseTemplate(this, node.seo) {
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
                                    // TODO format date?!
                                }
                                h4("subtitle is-size-6 is-bold") { +child.date.toString() }
                                div("content") {
                                    // TODO generate summary?
                                    markdown(child.body)
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

fun HTMLTag.markdown(content: Markdown) {
    unsafe { +content.toString() }
}

fun RenderContext<Article>.blogArticle() {
    out.appendHTML().baseTemplate(this, node.seo) {
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
                    +node.date.toString()
                }
            }
        }

        div("container") {
            div("section") {
                div("columns") {
                    div("column is-offset-2 is-8") {
                        div("content has-drop-caps") {
                            markdown(node.body)
                        }
                    }
                }
            }
        }
    }
}
