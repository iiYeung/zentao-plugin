package com.github.darylyeung.zentaoplugin.extension.zentao

import com.github.darylyeung.zentaoplugin.extension.icon.TasksCoreIcons
import com.github.darylyeung.zentaoplugin.extension.zentao.model.ZentaoBug
import com.github.darylyeung.zentaoplugin.extension.zentao.model.ZentaoProduct
import com.intellij.tasks.Comment
import com.intellij.tasks.Task
import com.intellij.tasks.TaskRepository
import com.intellij.tasks.TaskType
import java.util.*
import javax.swing.Icon

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-18 22:21:39
 */
class ZentaoTask() : Task() {
    private lateinit var myZentaoBug: ZentaoBug
    private lateinit var myZentaoRepository: ZentaoRepository
    private lateinit var myZentaoProduct: ZentaoProduct

    constructor(zentaoRepository: ZentaoRepository, zentaoBug: ZentaoBug) : this() {
        myZentaoBug = zentaoBug
        myZentaoRepository = zentaoRepository
        zentaoRepository.getProducts().forEach { product ->
            if (product.id == zentaoBug.product) {
                myZentaoProduct = product
            }
        }
    }

    override fun getId(): String {
        return myZentaoBug.id.toString()
    }

    override fun getPresentableId(): String {
        return myZentaoBug.id.toString()
    }

    override fun getSummary(): String {
       return myZentaoBug.title
    }

    override fun getDescription(): String {
        return myZentaoBug.title
    }

    override fun getComments(): Array<Comment> {
        return emptyArray()
    }

    override fun getIcon(): Icon {
        return TasksCoreIcons.zentao
    }

    override fun getType(): TaskType {
        return TaskType.BUG
    }

    override fun getUpdated(): Date? {
        TODO("Not yet implemented")
    }

    override fun getCreated(): Date? {
        TODO("Not yet implemented")
    }

    override fun isClosed(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isIssue(): Boolean {
        return true
    }

    override fun getIssueUrl(): String? {
        TODO("Not yet implemented")
    }

    override fun getProject(): String {
        return myZentaoProduct.name
    }

    override fun getRepository(): TaskRepository {
       return myZentaoRepository
    }
}