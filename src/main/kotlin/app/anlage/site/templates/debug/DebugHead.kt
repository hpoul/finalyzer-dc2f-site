package app.anlage.site.templates.debug

import com.dc2f.render.RenderContext
import kotlinx.html.*

fun HEAD.debugHead(context: RenderContext<*>) {
    unsafe {
        raw("""
<style>
    .debug-meta-description {
        margin-top: 100px;
        margin-bottom: 100px;
        padding: 10px;
        border: 10px solid #eee;
    }
    .debug-meta-description h3 {
        font-size: x-small;
    }
</style>
<script>
    document.addEventListener('DOMContentLoaded', () => {
        let metaDescription = document.querySelector('meta[name=description]').getAttribute("content");
        document.querySelector('footer').insertAdjacentHTML('beforebegin', '<div class="debug-meta-description"><h3>DEBUG - META DESCRIPTION: (not visible in production - hopefully)</h3><div class="content"></div></div>');
        document.querySelector('.debug-meta-description .content').textContent = metaDescription;
    });
</script>
        """)
    }
}