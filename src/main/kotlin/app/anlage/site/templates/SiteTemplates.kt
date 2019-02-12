package app.anlage.site.templates

import app.anlage.site.contentdef.*
import com.dc2f.assets.ScssTransformer
import com.dc2f.render.RenderContext
import kotlinx.html.*


fun <T> TagConsumer<T>.baseTemplate(
    context: RenderContext<*>,
    seo: PageSeo,
    mainContent: MAIN.() -> Unit
) =
    scaffold(context, seo) {
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
                        (context.rootNode as FinalyzerWebsite).children.map { folder ->
                            folder.menu?.let { menu ->
                                div("navbar-item") {
                                    a(context.href(folder)) {
                                        +menu.name
                                    }
                                }
                            }
                        }

                        div("navbar-item") {
                            // TODO signup url?
                            aButton(href = "signup", target = "_blank", label = "Sign Up!")
                        }
                    }
                }
            }
        }

        main {
            mainContent()
        }
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

fun <T> TagConsumer<T>.scaffold(context: RenderContext<*>, seo: PageSeo, body: BODY.() -> Unit) =
    html {
        head {
            siteHead(context, seo)
        }
        body("has-navbar-fixed-top has-spaced-navbar-fixed-top") {
            body()
        }
    }
