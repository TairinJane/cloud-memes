package com.tairin.cloudmemes.services

import com.tairin.cloudmemes.model.GoogleOAuth2UserInfo
import com.tairin.cloudmemes.model.User
import com.tairin.cloudmemes.repositories.UsersRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate


interface UserService {
    fun getCurrentUser(): User
}

@Component
class UserServiceImpl : UserService {

    @Autowired
    private lateinit var authorizedClientService: OAuth2AuthorizedClientService

    @Autowired
    private lateinit var restTemplate: RestTemplate

    @Autowired
    private lateinit var usersRepository: UsersRepository

    override fun getCurrentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication as OAuth2AuthenticationToken

        val client: OAuth2AuthorizedClient = authorizedClientService
            .loadAuthorizedClient(authentication.authorizedClientRegistrationId, authentication.name)

        val userInfoEndpointUri = client.clientRegistration.providerDetails.userInfoEndpoint.uri

        if (!userInfoEndpointUri.isNullOrEmpty()) {
            //TODO: custom exceptions
            val headers = HttpHeaders().apply {
                add(HttpHeaders.AUTHORIZATION, "Bearer ${client.accessToken.tokenValue}")
            }
            val entity: HttpEntity<String> = HttpEntity("", headers)
            val response: ResponseEntity<GoogleOAuth2UserInfo> =
                restTemplate.exchange(userInfoEndpointUri, HttpMethod.GET, entity, GoogleOAuth2UserInfo::class.java)

            val userInfo = response.body ?: throw Exception("User not found by token")

            if (!usersRepository.existsByUsername(userInfo.name)) {
                val user = User(userInfo.name, userInfo.email)
                usersRepository.save(user)
            }

            return usersRepository.getUserByUsername(userInfo.name)

        } else throw Exception("No Google OAuth URI found")
    }
}
