package app.anlage.site.templates

import app.anlage.site.contentdef.*
import com.dc2f.assets.ScssTransformer
import com.dc2f.render.RenderContext
import com.dc2f.util.toStringReflective
import com.google.common.net.MediaType
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

enum class Dc2fEnv(val id: String) {
    Production("production"),
    ProductionDrafts("production-drafts"),
    Dev("dev"),
    ;

    companion object {
        fun findById(id: String?, default: Dc2fEnv = Dc2fEnv.Dev) =
            id?.let { idString -> Dc2fEnv.values().firstOrNull { it.id == idString } }
                ?: default
    }
}

fun HEAD.siteHead(context: RenderContext<*>, seo: PageSeo) {
    val website = context.rootNode as FinalyzerWebsite
    val title = "${seo.title} | Anlage.App"
    title {
        +title
    }
    property("og:title", title)

    val env = Dc2fEnv.findById(System.getenv("DC2F_ENV"))

    script {
        unsafe {
            raw(
                """window.AA = { 'backendUrl': '${if (env == Dc2fEnv.Dev) {
                    website.config.backendUrlDev
                } else {
                    website.config.backendUrlProd
                }}' };"""
            )
        }
    }

    if (env == Dc2fEnv.Production) {
        unsafe {
            raw(
                """
<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-91100035-3"></script>
<script>
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());

    gtag('config', 'UA-91100035-3', {'siteSpeedSampleRate': 100});
</script>
            """.trimIndent()
            )
        }
    } else {
        unsafe {
            raw(
                """
<!-- Global site tag (gtag.js) - Google Analytics -->
<script async src="https://www.googletagmanager.com/gtag/js?id=UA-91100035-4"></script>
<script>
    window.dataLayer = window.dataLayer || [];
    function gtag(){dataLayer.push(arguments);}
    gtag('js', new Date());

    gtag('config', 'UA-91100035-4', {'siteSpeedSampleRate': 100});
</script>
                """.trimIndent()
            )
        }
    }

    meta(charset = "UTF-8")

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

    meta("viewport", "width=device-width, initial-scale=1")

    website.config.favicons.map { favicon ->
        @Suppress("UnstableApiUsage")
        when (val mediaType = MediaType.parse(favicon.image.imageInfo.mimeType).withoutParameters()) {
            // don't ask why, but i'll prefer image/x-icon over image/vnd.microsoft.icon for now.
            MediaType.ICO -> link(rel = "icon", type = "image/x-icon", href = favicon.image.href(context))
            else -> link(rel = "icon", type = mediaType.toString(), href = favicon.image.href(context)) {
                sizes = "${favicon.image.imageInfo.width}x${favicon.image.imageInfo.height}"
            }
        }
    }

    property("fb:app_id", "1950393328594234")
    property("twitter:title", title)
    property("twitter:card", "summary")
    property("twitter:site", "@AnlageApp")

    meta(name = "description", content = seo.description)
    property("og:description", seo.description)
    property("twitter:description", seo.description)

    property("og:url", context.href(context.node))

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
    val website = context.rootNode as FinalyzerWebsite
    footer("footer") {
        div("container") {
            div("columns") {
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


                div("column content has-text-right") {
                    p {
                        unsafe { +"""<strong>ANLAGE.APP</strong> by <a href="https://codeux.design/" target="_blank">codeux.design</a> and Herbert Poul""" }
                    }
                    p {
                        unsafe { +"""Questions? Suggestions? <a href="mailto:hello@anlage.app">hello@anlage.app</a>""" }
                    }
                }

            }

        }

        richText(
            context,
            (website.footerContent.referencedContent as? Partial)?.html,
            mapOf("type" to "footer")
        )
    }
}

fun <T> TagConsumer<T>.scaffold(
    context: RenderContext<*>,
    seo: PageSeo,
    headInject: HEAD.() -> Unit = {},
    body: BODY.() -> Unit
) =
    html {
        lang = "en-us"
        head {
            siteHead(context, seo)
            headInject()
        }
        body("has-navbar-fixed-top has-spaced-navbar-fixed-top") {
            body()
        }
    }
