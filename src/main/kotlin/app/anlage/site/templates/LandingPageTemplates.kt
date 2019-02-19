@file:Suppress("UnstableApiUsage")

package app.anlage.site.templates

import app.anlage.site.contentdef.*
import com.dc2f.FillType
import com.dc2f.render.*
import com.google.common.net.MediaType
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

enum class ButtonType(val classes: String) {
    Primary("is-primary"),
}

fun FlowContent.aButton(
    type: ButtonType?,
    href: String? = null,
    target: String? = null,
    label: String,
    iconClasses: String? = null,
    block: A.() -> Unit = {}
) {
    a(href, target, classes = "button ${type?.classes ?: ""}") {
        iconClasses?.let { icon(iconClasses) }
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
//        div {
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
                                            a(
                                                "#start-element",
                                                classes = "button is-primary is-large"
                                            ) {
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
                                    if (child.leftAlign) {
                                        classes = classes + "columns-reversed"
                                    }
                                    div("column is-7") {
                                        // TODO add image resizing/optimization stuff
                                        child.screenshot.resize(context, 1200, Int.MAX_VALUE, FillType.Fit).let { image ->
                                            figure("image screenshot") {
                                                attributes["data-aos"] = "fade-up"
                                                attributes["data-name"] = child.screenshot.name
                                                img {
                                                    src = image.href
                                                    alt = child.title
                                                    width = image.width.toString()
                                                    height = image.height.toString()
                                                }
                                            }
                                        }
                                    }
                                    div("column content") {
                                        if (child.bodyTextAlign == TextAlign.Center) {
                                            classes = classes + "has-text-centered"
                                        }
                                        h3 { +child.title }
                                        markdown(context, child.body)
//                                        unsafe {
//                                            +child.body.toString()
//                                        }
                                    }
                                }
                            }
                        }
                    }

                    is LandingPageElement.Start -> {
                        section("section has-background-primary-light") {
                            div("anchor") {
                                div {
                                    id = "start-element"
                                    attributes["data-target"] = "start-element-input"
                                }
                            }

                            div("container") {
                                div("columns") {
                                    div("column has-text-centered") {
                                        div("is-size-3") { +child.title }
                                        img(
                                            src = context.getAsset("theme/images/arrow.svg")
                                                .href(RenderPath.parse("/images/")
                                            ),
                                            alt = "Arrow Image"
                                        ) {}
                                        h4("subtitle is-size-5 is-bold") { +child.subTitle }
                                    }
                                    div("column has-text-centered") {
                                        div("is-size-3 email-form-spacing") { unsafe { raw("&nbsp;") } }
                                        form(classes = "email-form") {
                                            div("field") {
                                                div("control has-icons-left") {
                                                    span("icon is-small is-left") {
                                                        i("fas fa-user")
                                                    }
                                                    textInput(name = "email", classes = "input") {
                                                        id = "start-element-input"
                                                        placeholder = "Email Address"
                                                    }
                                                }
                                            }
                                            submitInput(classes = "button is-primary") {
                                                value = "Sign Up for free"
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
//        }
    }
}