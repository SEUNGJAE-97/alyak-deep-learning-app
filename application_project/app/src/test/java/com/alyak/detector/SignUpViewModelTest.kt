package com.alyak.detector

import com.alyak.detector.ui.signUp.SignUpViewModel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SignUpViewModelTest {

    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setup() {
        viewModel = SignUpViewModel()
    }

    @Test
    fun `validateEmail should return true for valid email`() = runTest {
        viewModel.validateEmail("sj_hahaha@naver.com")
        assertEquals(true, viewModel.state.value.validEmail)
    }

    @Test
    fun `validatePhoneNumber should return true for valid phone number`() = runTest {
        viewModel.validatePhoneNumber("010-1234-5678")
        assertEquals(true, viewModel.state.value.validPhoneNumber)
    }

    @Test
    fun `validatePassword should return true for valid password`() = runTest {
        viewModel.validatePassword("Abc12345!")
        assertEquals(true, viewModel.state.value.validPassword)
    }

    @Test
    fun `validateSSN should return true for valid SSN`() = runTest {
        viewModel.validateSSN("123456-1234567")
        assertEquals(true, viewModel.state.value.validSSN)
    }
}
