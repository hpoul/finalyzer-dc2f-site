/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package app.anlage.site

import com.dc2f.*
import com.dc2f.example.*
import mu.KotlinLogging
import java.nio.file.FileSystems
import org.apache.commons.lang3.builder.*

private val logger = KotlinLogging.logger {}

abstract class FinalyzerWebsite: Website<SimpleContentFolderChild>

fun main(args: Array<String>) {
    logger.info { "Starting ..." }

    val website = ContentLoader(FinalyzerWebsite::class)
        .load(FileSystems.getDefault().getPath("web", "content"))
    logger.info { "loaded website ${website}."}
    logger.info { "reflected: ${ReflectionToStringBuilder.toString(website, RecursiveToStringStyle())}" }

}
