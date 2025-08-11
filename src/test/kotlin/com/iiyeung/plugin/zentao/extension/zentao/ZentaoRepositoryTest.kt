package com.iiyeung.plugin.zentao.extension.zentao

import com.intellij.openapi.diagnostic.thisLogger
import org.junit.Before
import org.junit.Test

/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-20 13:19:50
 */
class ZentaoRepositoryTest {

    private val zentaoRepository = ZentaoRepository()

    @Before
    fun setUp() {
        zentaoRepository.generateAndStoreToken()
        thisLogger().info("setUp: ${zentaoRepository.getCurrentToken()}")
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
        zentaoRepository.generateAndStoreToken()
        thisLogger().info("generateToken: ${zentaoRepository.getCurrentToken()}")
    }
}