package com.iiyeung.plugin.zentao.extension.zentao

import com.intellij.openapi.diagnostic.thisLogger
import org.junit.Before
import org.junit.Test

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-20 13:19:50
 */
class ZentaoRepositoryTest {

    private val zentaoRepository = ZentaoRepository()

    @Before
    fun setUp() {
        zentaoRepository.generateToken()
        thisLogger().info("setUp: ${zentaoRepository.getToken()}")
    }

    @Test
    fun getProducts() {
        val products = zentaoRepository.getProducts()
        thisLogger().info("getProducts: $products")
    }

    @Test
    fun fetchProducts() {
        val products = zentaoRepository.fetchProducts()
        thisLogger().info("fetchProducts: $products")
    }

    @Test
    fun generateToken() {
        zentaoRepository.generateToken()
        thisLogger().info("generateToken: ${zentaoRepository.getToken()}")
    }
}