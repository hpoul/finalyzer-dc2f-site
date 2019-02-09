package app.anlage.site

import com.dc2f.render.RenderContext
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

fun FlowContent.aButton(href: String? = null, target: String? = null, label: String, block: A.() -> Unit = {}) {
    a(href, target, classes = "button") {
        span { +label }
        block()
    }
}

fun FlowContent.icon(classes: String) {
    span("icon") {
        i(classes) {
            attributes["aria-hidden"] = "true"
        }
    }
}

fun RenderContext<LandingPage>.landingPage() {
    out.appendHTML().baseTemplate(node.seo) {
        h1 {
            +"Hello World."
        }
        div {
            node.children.map { child ->
                when (child) {
                    is LandingPageElement.Intro -> {
                        div("homepage-hero-module") {
                            div("video-container") {
                                // TODO video stuff
                            }
                            div("hero-module-content") {
                                div("section") {
                                    div("has-text-centered") {
                                        h1("title") { +child.teaser }
                                        h2("subtitle") {
                                            +"Success per stock. Multiple Currencies. Compare Performance. Monthly Reports."
                                        }
                                        div {
                                            a("#start-element", classes = "button is-primary is-large") {
                                                child.buttonLabel
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    is LandingPageElement.Hero -> {
                        section("landing-hero-element section") {
                            div("container") {
                                div("columns is-vcentered") {
                                    classes = classes + ""
                                    div("column is-7") {
                                        // TODO image stuff
                                    }
                                    div("column content") {
                                        h3 { +child.title }
                                        +child.body.toString()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}