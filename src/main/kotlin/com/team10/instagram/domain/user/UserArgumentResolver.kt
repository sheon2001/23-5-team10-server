package com.team10.instagram.domain.user

import com.team10.instagram.domain.user.model.User
import com.team10.instagram.domain.user.repository.UserRepository
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class UserArgumentResolver(
    private val userRepository: UserRepository,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasAnnotation = parameter.hasParameterAnnotation(LoggedInUser::class.java)
        // 파라미터 타입이 User(Entity) 이거나 Long(ID) 인지 확인
        val hasType =
            User::class.java.isAssignableFrom(parameter.parameterType) ||
                Long::class.java.isAssignableFrom(parameter.parameterType) ||
                parameter.parameterType == Long::class.javaPrimitiveType

        return hasAnnotation && hasType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? { // User 또는 Long 반환 가능

        val userId =
            webRequest.getAttribute("userId", RequestAttributes.SCOPE_REQUEST) as? Long
                ?: return null // 없으면 null 반환

        // 원하는 타입이 User Entity라면 DB 조회 후 반환
        if (User::class.java.isAssignableFrom(parameter.parameterType)) {
            return userRepository.findByUserId(userId)
        }

        // 원하는 타입이 Long이라면 ID만 바로 반환
        return userId
    }
}
