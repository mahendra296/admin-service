package com.admin.mapper

import com.admin.dto.ImageDTO
import com.admin.dto.ProductDTO
import com.admin.dto.ProductRequestDTO
import com.admin.dto.VariantDTO
import com.admin.entity.Product
import com.admin.entity.ProductImage
import com.admin.entity.ProductVariant
import com.admin.utils.DateUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.mapstruct.*
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDate
import java.time.ZonedDateTime


@Mapper(componentModel = "spring", imports = [ZonedDateTime::class, DateUtils::class], unmappedTargetPolicy = ReportingPolicy.IGNORE)
abstract class  ProductMapper {

    @Mappings(
        Mapping(source = "publishedAt", target = "publishedAt", qualifiedByName = ["convertToDate"]),
        Mapping(source = "createdAt", target = "createdAt", qualifiedByName = ["convertToDate"]),
        Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = ["convertToDate"])
    )
    abstract fun toProductEntity(request: ProductDTO): Product

    @Mappings(
        Mapping(source = "publishedAt", target = "publishedAt", qualifiedByName = ["convertStringToDate"])
    )
    abstract fun toProduct(request: ProductRequestDTO): Product

    @Mappings(
        Mapping(source = "createdAt", target = "createdAt", qualifiedByName = ["convertToDate"]),
        Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = ["convertToDate"]),
        Mapping(source = "featuredImage", target = "featuredImage", qualifiedByName = ["convertObjectTOString"])
    )
    abstract fun toProductVariantEntity(request: VariantDTO): ProductVariant

    @Mappings(
        Mapping(source = "createdAt", target = "createdAt", qualifiedByName = ["convertToDate"]),
        Mapping(source = "updatedAt", target = "updatedAt", qualifiedByName = ["convertToDate"])
    )
    abstract fun toProductImageEntity(request: ImageDTO): ProductImage

    @Named("convertToDate")
    protected fun convertToZonedDate(dateString: String?): ZonedDateTime? {
        return DateUtils.convertToDate(dateString)
    }

    @Named("convertStringToDate")
    protected fun convertStringToDate(dateString: String?): ZonedDateTime? {
        return DateUtils.convertStringToDate(dateString)
    }

    @Named("convertObjectTOString")
    protected fun convertObjectTOString(obj: Any?): String? {
        return obj?.let { ObjectMapper().writeValueAsString(obj) }
    }
}
