package app.anlage.site.templates

import app.anlage.site.contentdef.*
import com.dc2f.assets.ScssTransformer
import com.dc2f.render.RenderContext
import com.dc2f.util.toStringReflective
import kotlinx.html.*

fun <TAG, T : WithPageSeo> TagConsumer<TAG>.baseTemplate(
    context: RenderContext<T>,
    headInject: HEAD.() -> Unit = {},
    mainContent: MAIN.() -> Unit
) = baseTemplate(context, context.node.seo, headInject, mainContent)


fun <T> TagConsumer<T>.baseTemplate(
    context: RenderContext<*>,
    seo: PageSeo,
    headInject: HEAD.() -> Unit = {},
    mainContent: MAIN.() -> Unit
) =
    scaffold(context, seo, headInject) {
        val website = context.rootNode as FinalyzerWebsite
        nav("navbar has-shadow is-spaced is-fixed-top") {
            role = "navigation"
            attributes["aria-label"] = "main navigation"
            div("container") {
                div("navbar-brand") {
                    a("/", classes = "navbar-item") {
                        // TODO image stuff
                        img(
                            "ANLAGE.APP",
                            src = context.getAsset("theme/images/logo-anlage-app.svg").href("images/logo-anlage-app.svg")
                        )
                    }

                    a(classes = "navbar-burger") {
                        role = "button"
                        attributes["aria-label"] = "menu"
                        attributes["aria-expanded"] = "false"
                        span { }
                        span { }
                        span { }
                    }
                }
                div("navbar-menu") {
                    id = "main-menu"
                    div("navbar-end") {
//                        website.children.flatMap {
//                            (it as? ContentPageFolder)?.children?.plus(it) ?: listOf(it)
//                        }.map { folder ->
//                            folder.menu?.let { menu ->
//                                a(context.href(folder), classes = "navbar-item") {
//                                    +menu.name
//                                }
//                            }
//                        }
                        website.mainMenu.map { entry ->
                            a(entry.href(context), classes = "navbar-item") {
                                +entry.linkLabel
                            }
                        }

                        div("navbar-item") {
                            // TODO signup url?
                            aButton(
                                type = ButtonType.Primary,
                                href = "signup",
                                target = "_blank",
                                iconClasses = "fas fa-sign-in",
                                label = "Sign Up!"
                            )
                        }
                    }
                }
            }
        }

        main {
            mainContent()
        }

        siteFooter(context)
    }

fun HEAD.property(propertyName: String, content: String) {
    meta(content = content) {
        attributes["property"] = propertyName
    }
}

fun HEAD.siteHead(context: RenderContext<*>, seo: PageSeo) {
    title {
        +seo.title
    }
    property("og:title", seo.title)
    property("fb:app_id", "1950393328594234")
    property("twitter:title", seo.title)
    property("twitter:card", "summary")
    property("twitter:site", "@AnlageApp")

    link(rel = LinkRel.stylesheet) {
        href = context.getAsset("theme/scss/main.scss")
            .transform(ScssTransformer())
            .href("styles/css/main.css")
    }
    script(
        // TODO add support for typescript transform?
        type = ScriptType.textJavaScript,
        src = context.getAsset("theme/script/main.js").href("script/main.js")
    ) {

    }

    meta(name = "description", content = seo.description)
    property("og:description", seo.description)
    property("twitter:description", seo.description)

    // <meta content="text/html;charset=utf-8" http-equiv="Content-Type">
    // <meta content="utf-8" http-equiv="encoding">
    meta(content = "text/html;charset=utf-8") {
        httpEquiv = MetaHttpEquiv.contentType
    }
    meta(content = "utf-8") {
        httpEquiv = "encoding"
    }

}

private fun MenuEntry.href(context: RenderContext<*>): String? =
    this.ref?.href(context) ?: this.url

fun <T> TagConsumer<T>.siteFooter(context: RenderContext<*>) {
    footer("footer") {
        div("container") {
            div("columns") {
                // TODO site menu
                (context.rootNode as? FinalyzerWebsite)?.let { website ->
                    website.footerMenu.map { menu ->
                        div("column") {
                            span("footer-title title is-4") { +menu.name }

                            ul {
                                menu.children.map { entry ->
                                    li {
                                        a(entry.href(context)) {
                                            +entry.linkLabel
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


                div("column content has-text-right") {
                    p {
                        unsafe { +"""<strong>ANLAGE.APP</strong> by <a href="https://codeux.design/" target="_blank">codeux.design</a> and Herbert Poul""" }
                    }
                    p {
                        unsafe { +"""Questions? Suggestions? <a href="mailto:hello@anlage.app">hello@anlage.app</a>"""}
                    }
                }

            }

        }
    }
}

fun <T> TagConsumer<T>.scaffold(context: RenderContext<*>, seo: PageSeo, headInject: HEAD.() -> Unit = {}, body: BODY.() -> Unit) =
    html {
        head {
            siteHead(context, seo)
            headInject()
        }
        body("has-navbar-fixed-top has-spaced-navbar-fixed-top") {
            body()
        }
    }
