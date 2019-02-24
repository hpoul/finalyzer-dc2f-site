@file:Suppress("UnstableApiUsage")

package app.anlage.site.templates

import app.anlage.site.contentdef.*
import com.dc2f.FillType
import com.dc2f.render.*
import com.dc2f.util.then
import com.google.common.net.MediaType
import kotlinx.html.*

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
        // DIFF space in old implementation.
        +" "
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

private fun DIV.arrowImage(context: RenderContext<*>) {
    img(
        src = context.getAsset("theme/images/arrow.svg")
            .href(RenderPath.parse("/images/")
            ),
        alt = "Arrow Image"
    ) {}
}

fun HEAD.cpcLandingPageHeadShopScripts(context: RenderContext<CpcLandingPage>) {
    unsafe { raw("""
    <script>
        var fscSession = {
            'reset': true,
            'coupon': '${context.node.couponCode}'

        };
        function decorateURL(url) {
            try {
                var linkerParam = null;
                ga(function () {
                    var trackers = ga.getAll();
                    linkerParam = trackers[0].get('linkerParam');
                });
                return (linkerParam ? url + '?' + linkerParam : url);
            } catch (e) {
                console.error('error while decorating URL', e);
                return url;
            }
        }
        function fastspringDataCallback() {

        }
    </script>
    <script
            id="fsc-api"
            src="https://d1f8f9xcsvx3ha.cloudfront.net/sbl/0.7.6/fastspring-builder.min.js"
            type="text/javascript"
            data-storefront="codeuxdesign.onfastspring.com/popup-codeuxdesign"
            data-debug="true"
            data-data-callback="fastspringDataCallback"
            data-decorate-callback="decorateURL"
            defer>
    </script>
    <!-- DO NOT MODIFY -->
    <!-- Quora Pixel Code (JS Helper) -->
    <script>
        !function(q,e,v,n,t,s){if(q.qp) return; n=q.qp=function(){n.qp?n.qp.apply(n,arguments):n.queue.push(arguments);}; n.queue=[];t=document.createElement(e);t.async=!0;t.src=v; s=document.getElementsByTagName(e)[0]; s.parentNode.insertBefore(t,s);}(window, 'script', 'https://a.quora.com/qevents.js');
        qp('init', 'eae54d9d4de74813ac479f7c6427b6a1');
        qp('track', 'ViewContent');
    </script>
    <noscript><img height="1" width="1" style="display:none" src="https://q.quora.com/_/ad/eae54d9d4de74813ac479f7c6427b6a1/pixel?tag=ViewContent&noscript=1"/></noscript>
    <!-- End of Quora Pixel Code -->
    """) }
}

fun RenderContext<CpcLandingPage>.cpcNavbarOverride(): (DIV.() -> Unit) = {
    val website = rootNode as FinalyzerWebsite
    div("navbar-item") {
        a(website.config.signUpUrl, ATarget.blank, classes = "button is-primary") {
            // TODO make this configurable?
            attributes["data-fsc-action"] = "Add,Checkout"
            attributes["data-fsc-item-path-value"] = "anlage-app-premium-sub"

            icon("fas fa-sign-in")
            // DIFF added newline to minimize diff with hugo version.
            +" "
            span { +node.ctaBuyNowLabel }
        }
    }
}

fun RenderContext<CpcLandingPage>.landingPage() {
    appendHTML().baseTemplate(
        this,
        node.seo,
        // we only need to inject shop JS if there is at least one [CpcTry] element on the page.
        headInject = { node.children.find { it is LandingPageElement.CpcTry }?.let { cpcLandingPageHeadShopScripts(context) } },
        navbarMenuOverride = node.overrideNavbar.then { context.cpcNavbarOverride() }
    ) {
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
                                div("poster") {
                                    style = "background-image: url('${child.backgroundVideo.placeholder.href(context)}')"
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
                        // DIFF added a useless div here, for minimizing diffs
                        section("landing-hero-element section") { div("") {
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
                                                    // DIFF for compatibility. but maybe we should use child.title instead of file name.
//                                                    alt = child.title
                                                    alt = child.screenshot.name
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
                        } }
                    }

                    is LandingPageElement.CpcTry -> {
                        section("section has-background-primary-light") {
                            div("container") {
                                div("columns") {
                                    div("column is-6 has-text-centered") {
                                        div {
                                            h3("title") { +child.offerTitle }
                                            h4("subtitle") { markdown(context, child.offerSubTitle, asInlineContent = true) }
                                        }
                                        arrowImage(context)
                                        div {
                                            div("has-text-weight-bold has-text-danger is-size-4") {
                                                style = "text-decoration: line-through"
                                                attributes["data-fsc-item-path"] = "anlage-app-premium-sub"
                                                span {
                                                    attributes["data-fsc-item-path"] = "anlage-app-premium-sub"
                                                    attributes["data-fsc-smartdisplay"] = ""
                                                    attributes["data-fsc-item-priceTotal"] = ""
                                                }
                                            }
                                            span("plan-price-amount") {
                                                span("has-text-success is-size-3 has-text-weight-bold") {
                                                    attributes["data-fsc-item-path"] = "anlage-app-premium-sub"
                                                    attributes["data-fsc-item-total"] = ""
                                                    +"$10"
                                                }
                                                +"/month"
                                            }
                                        }
                                    }

                                    div("column is-6 has-text-centered") {
                                        div("is-size-4 email-form-spacing") { unsafe { +"&nbsp;" } }
                                        div("is-size-4 email-form-spacing") { unsafe { +"&nbsp;" } }

                                        form(classes = "email-form") {
                                            hiddenInput(name = "fs_coupon") {
                                                value = child.offerCoupon
                                            }
                                            div("field") {
                                                div("control has-icons-left") {
                                                    span("icon is-small is-left") {
                                                        i("fas fa-user")
                                                    }
                                                    // DIFF added newline to minimize diff with hugo version.
                                                    +" "
                                                    textInput(classes = "input") {
                                                        name = "email"
                                                        placeholder = "Email Address"
                                                    }
                                                }
                                            }

                                            submitInput(classes = "button is-success is-large") {
                                                attributes["data-aos"] = "wiggle"
                                                value = "Save Now and Sign Up!"
                                            }
                                        }
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
                                        arrowImage(context)
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
                                                    // DIFF useless space on old page.
                                                    +" "
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
                    is LandingPageElement.NotReady -> unsafe { raw("""
<section class="section">
    <div class="container">
        <div class="content has-text-centered">
            <p>
                Not ready yet to commit <span class="has-text-success has-text-weight-bold"
                                              data-fsc-item-path="anlage-app-premium-sub"
                                              data-fsc-item-total></span>?</p>
            <p>
                <a href="" class="button" data-fsc-action="Reset,Add,Checkout" data-fsc-item-path-value="anlage-app-free-sub">Start free Trial</a>
            </p>
        </div>
    </div>
</section>
                    """)
                    }
                    is LandingPageElement.Content ->
                        section("section") {
                            div("container content") {
                                richText(context, child.body)
                            }
                        }

                }.let {  } // https://discuss.kotlinlang.org/t/sealed-classes-and-when-expressions/3980
            }
        }
    }
