package app.anlage.site.templates

import app.anlage.site.contentdef.*
import app.anlage.site.templates.debug.debugHead
import com.dc2f.assets.*
import com.dc2f.render.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.MediaType
import kotlinx.html.*
import kotlinx.html.dom.*
import java.io.File

fun <TAG, T : WithPageSeo> TagConsumer<TAG>.baseTemplate(
    context: RenderContext<T>,
    headInject: HEAD.() -> Unit = {},
    mainContent: MAIN.() -> Unit
) = baseTemplate(context, context.node.seo, headInject, mainContent = mainContent)


fun <T> TagConsumer<T>.baseTemplate(
    context: RenderContext<*>,
    seo: PageSeo,
    headInject: HEAD.() -> Unit = {},
    navbarMenuOverride: (DIV.() -> Unit)? = null,
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
                            src = context.getAsset("theme/images/logo-anlage-app.svg")
                                .href(RenderPath.parse("/images/"))
                        )
                    }
                    // DIFF added newline to minimize diff with hugo version.
                    +" "

                    a(classes = "navbar-burger") {
                        role = "button"
                        attributes["data-target"] = "main-menu"
                        attributes["aria-label"] = "menu"
                        attributes["aria-expanded"] = "false"
                        // DIFF added newline to minimize diff with hugo version.
                        +" "
                        span { attributes["aria-hidden"] = "true" }; +" "
                        span { attributes["aria-hidden"] = "true" }; +" "
                        span { attributes["aria-hidden"] = "true" }
                    }
                }
                div("navbar-menu") {
                    id = "main-menu"
                    div("navbar-end") {
                        if (navbarMenuOverride == null) {
                            val active = website.mainMenu.findActiveEntry(context.renderer.loaderContext, context.node)?.let { activeEntry ->
                                if (activeEntry.ref?.referencedContentPath(context.renderer.loaderContext)?.isRoot == true && activeEntry.ref?.referencedContent != context.node) {
                                    null
                                } else {
                                    activeEntry
                                }
                            }
                            website.mainMenu.map { entry ->
                                a(entry.href(context), classes = "navbar-item") {
                                    entry.ref?.referencedContent?.let { ref ->
                                        if (active == entry) {
                                            classes = classes + "is-active"
                                        }
                                    }
                                    title = entry.linkLabel
                                    +entry.linkLabel
                                }
                                // DIFF space in old implementation.
                                +" "
                            }

                            div("navbar-item") {
                                aButton(
                                    type = ButtonType.Primary,
                                    href = website.config.signUpUrl,
                                    target = "_blank",
                                    iconClasses = "fas fa-sign-in",
                                    label = "Sign Up"
                                )
                            }
                        } else {
                            navbarMenuOverride()
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
        debugHead(context)
        unsafe {
            raw("""
<script type="text/javascript">
    (function (g) {
        var s = document.createElement('script'),
            t = document.getElementsByTagName('script')[0];
        s.async = true;
        s.src = g + '?v=' + (new Date()).getTime();
        s.charset = 'UTF-8';
        s.setAttribute('crossorigin', '*');
        t.parentNode.insertBefore(s, t);
    })('https://www.canvasflip.com/plugins/vi/vi.min.js');
</script>
            """)
        }
    }

    meta(charset = "UTF-8")

    link(rel = LinkRel.stylesheet.toLowerCase()) {
        val digest = DigestTransformer()
        href = context.getAsset("theme/scss/main.scss")
            .transform(
                ScssTransformer(
                    includePaths = listOf(
                        File("."),
                        File(context.getResourceFromFileSystem("theme/scss/"))
                    )
                )
            ).transform(digest)
            .href(RenderPath.parse("/styles/css/"))
        integrity = requireNotNull(digest.value?.integrityAttrValue)
    }
    script(
        // TODO add support for typescript transform?
        // DIFF don't include type=".."
//        type = ScriptType.textJavaScript,
        src = context.getAsset("theme/script/main.js").href(RenderPath.parse("/script/"))
    ) {
        async = true
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
    // DIFF for compatibility with hugo site, we use the bare seo.title
//    property("twitter:title", title)
    property("twitter:title", seo.title)
    property("twitter:card", "summary")
    property("twitter:site", "@AnlageApp")

    if (seo.description.isNotBlank()) {
        // DIFF we should probably not support empty descriptions.
        meta(name = "description", content = seo.description)
        property("og:description", seo.description)
        property("twitter:description", seo.description)
    }

    property("og:url", context.href(context.node, true))

    if (seo.noIndex == true) {
        meta("robots", "noindex")
    }

    @Suppress("SimplifiableCallChain")
    val linkedData = if (website.index == context.node) {
        mapOf(
            "@context" to "http://schema.org",
            "@type" to "Product",
            "url" to context.href(website, true),
            "name" to "Anlage.App",
            "logo" to website.config.logo?.href(context, absoluteUri = true)
        )
    } else {
        LinkedHashMap<String, Any>().apply {
            put("@context", "http://schema.org")
            put("@type", if (context.node is Article) { "Article" } else { "WebPage" })
            put("headline", seo.title)
            put("mainEntityOfPage", mapOf(
                "@type" to "WebPage",
                "@id" to context.href(context.node, absoluteUrl = true)
            ))
            (context.node as? WithMainImage)?.mainImage()?.let { mainImage ->
                put("image", mainImage.href(context, true))
            }
            // DIFF we ignore "marketcap game" hardcoded here, because on hugo they had no category.
            (context.node as? Article)?.categories?.filter { it != "MarketCap Game" }?.firstOrNull()?.let { category ->
                put("articleSection", category)
            }
            (context.node as? WithWordCount)?.wordCount()?.let {
                put("wordcount", it)
            }
            put("url", context.href(context.node, true))
            // TODO: datePublished
            // TODO: dateModified
            put("publisher", LinkedHashMap<String, Any>().apply {
                put("@type", "Organization")
                put("name", website.name)
                put("logo", mapOf(
                    "@type" to "ImageObject",
                    "url" to website.config.logo?.href(context, absoluteUri = true)
                ))
            })
            (context.node as? WithAuthor)?.author ?.let { author ->
                put("author", mapOf(
                    "@type" to "Person",
                    "name" to author
                ))
            }
            put("description", seo.description)
        }
    }
    script("application/ld+json") {
        unsafe { raw(ObjectMapper().writeValueAsString(linkedData)) }
    }

    (context.node as? Article)?.let { article ->
        val image = article.mainImage() ?: article.teaser
        property("og:image", image.href(context, absoluteUri = true))
        property("og:image:width", image.width.toString())
        property("og:image:height", image.height.toString())
        property("twitter:image", image.href(context, absoluteUri = true))
        property("og:type", "article")
    }

//    meta(content = "text/html;charset=utf-8") {
//        httpEquiv = MetaHttpEquiv.contentType
//    }

//    meta(content = "utf-8") {
//        httpEquiv = "encoding"
//    }

}

private fun MenuEntry.href(context: RenderContext<*>): String? =
    this.ref?.href(context) ?: this.url

fun BODY.siteFooter(context: RenderContext<*>) {
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
    unsafe {
        raw("""
<!-- Load Facebook SDK for JavaScript -->
<div id="fb-root"></div>
<script>(function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = 'https://connect.facebook.net/en_US/sdk/xfbml.customerchat.js#xfbml=1&version=v2.12&autoLogAppEvents=1';
    js.async = true;
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>

<!-- Your customer chat code -->
<div class="fb-customerchat"
     attribution=setup_tool
     page_id="306833413221913">
</div>
        """)
    }
}

fun <T> TagConsumer<T>.scaffold(
    context: RenderContext<*>,
    seo: PageSeo,
    headInject: HEAD.() -> Unit = {},
    body: BODY.() -> Unit
) =
    document {

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
    }
