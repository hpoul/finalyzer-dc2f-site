package app.anlage.site

import com.dc2f.*
import com.dc2f.render.*
import com.fasterxml.jackson.databind.ObjectMapper
import java.nio.file.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


/**
 * TODO somehow combine this with the [com.dc2f.render.Renderer]?
 */
class AllSitesJsonGenerator(
    private val target: Path,
    private val loaderContext: LoaderContext,
    private val renderer: Renderer
) {

    fun render() {
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val pages = generateSequence(listOf(loaderContext.rootNode)) { nodes ->
            nodes.flatMap { node -> (node as? ContentBranchDef<*>)?.children ?: emptyList() }.ifEmpty { null }
        }.flatten().map { node ->

            mapOf(
                "lastmod" to formatter.format((node as? WithSitemapInfo)?.commitInfo?.authorDate ?: ZonedDateTime.now()),
                "url" to renderer.href(node, true))
        }.toList()
        Files.write(target, ObjectMapper().writeValueAsBytes(mapOf("pages" to pages)))
    }

}


