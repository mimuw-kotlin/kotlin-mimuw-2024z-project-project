package com.modules

import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

//class ApplicationTest {
//
//    @Test
//    fun testRoot() = testApplication {
//        environment {
//            config = MapApplicationConfig(
//                "session.secretEncryptKey" to "00112233445566778899aabbccddeeff",
//                "session.secretSignKey" to "6819b57a326945c1968f45236589"
//            )
//        }
//        application {
//            module()
//        }
//        client.get("/").apply {
//            assertEquals(HttpStatusCode.OK, HttpStatusCode.OK)
//        }
//    }
//
//}
