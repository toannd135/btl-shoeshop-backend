package vn.edu.ptit.shoe_shop.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.search.PermissionSearchRequestDTO;
import vn.edu.ptit.shoe_shop.entity.Permission;

import vn.edu.ptit.shoe_shop.entity.QPermission;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.PermissionRepositoryCustom;

import java.util.List;

@Repository
public class PermissionRepositoryCustomImpl implements PermissionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPermission permission;

    public PermissionRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.permission = QPermission.permission;
    }

    @Override
    public Page<Permission> searchPermissions(PermissionSearchRequestDTO request, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(request.getName() != null) {
            builder.or(permission.name.containsIgnoreCase(request.getName()));
        }
        if(request.getApiPath() != null) {
            builder.or(permission.apiPath.containsIgnoreCase(request.getApiPath()));
        }
        if(request.getMethod() != null) {
            builder.or(permission.method.eq(request.getMethod()));
        }
        if(request.getModule() != null) {
            builder.or(permission.module.containsIgnoreCase(request.getModule()));
        }
        if(request.getStatus() != null) {
            builder.or(permission.status.eq(StatusEnum.valueOf(request.getStatus())));
        }

        JPAQuery<Permission> query = this.jpaQueryFactory
                .selectFrom(permission)
                .where(builder);

        for(Sort.Order order : pageable.getSort()) {
            PathBuilder<Permission> pathBuilder = new PathBuilder<>(Permission.class, permission.getMetadata());
            query.orderBy(
                    new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(order.getProperty())
                    )
            );
        }

        List<Permission> contents = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = this.jpaQueryFactory
                .select(permission.count())
                .from(permission)
                .where(builder)
                .fetchOne();
        return new PageImpl<>(contents, pageable, total);
    }
}
