@file:Suppress("UnstableApiUsage")

package app.anlage.site

import com.dc2f.render.RenderContext
import com.google.common.net.MediaType
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
    out.appendHTML().baseTemplate(this, node.seo) {
        div {
            node.children.map { child ->
                when (child) {
                    is LandingPageElement.Intro -> {
                        div("homepage-hero-module") {
                            div("video-container") {
                                // TODO video stuff
                                div("filterx")
                                video("fillWidth is-hidden-mobile") {
                                    autoPlay = true
                                    loop = true
                                    attributes["muted"] = "muted"
                                    poster = child.backgroundVideo.placeholder.href(context)
                                    source {
                                        src = child.backgroundVideo.videoMp4.href(context)
                                        type = MediaType.MP4_VIDEO.toString()
                                    }
                                    source {
                                        src = child.backgroundVideo.videoWebm.href(context)
                                        type = MediaType.WEBM_VIDEO.toString()
                                    }
                                }
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
                                                +child.buttonLabel
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