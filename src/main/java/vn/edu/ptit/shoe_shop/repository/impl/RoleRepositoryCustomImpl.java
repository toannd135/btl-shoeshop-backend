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
import vn.edu.ptit.shoe_shop.dto.request.search.RoleSearchRequestDTO;
import vn.edu.ptit.shoe_shop.entity.QRole;
import vn.edu.ptit.shoe_shop.entity.Role;
import vn.edu.ptit.shoe_shop.repository.RoleRepositoryCustom;

import java.util.List;

@Repository
public class RoleRepositoryCustomImpl implements RoleRepositoryCustom {
    private final QRole role;
    private final JPAQueryFactory jpaQueryFactory;

    public RoleRepositoryCustomImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.role = QRole.role;
    }

    @Override
    public Page<Role> searchRoles(RoleSearchRequestDTO request, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if(request.getName() != null && !request.getName().isEmpty()) {
            builder.or(role.name.containsIgnoreCase(request.getName()));
        }
        if(request.getDescription() != null) {
            builder.or(role.description.containsIgnoreCase(request.getDescription()));
        }
        if(request.getStatus() != null) {
            builder.or(role.status.eq(StatusEnum.valueOf(request.getStatus())));
        }
        if(request.getCode() != null) {
            builder.or(role.code.containsIgnoreCase(request.getCode()));
        }

        JPAQuery<Role> query = this.jpaQueryFactory
                .selectFrom(role)
                .where(builder);

        for(Sort.Order order : pageable.getSort()) {
            PathBuilder<Role> pathBuilder = new PathBuilder<>(Role.class, role.getMetadata());
            query.orderBy(
                    new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(order.getProperty())
                    )
            );
        }

        List<Role> contents = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = this.jpaQueryFactory
                .select(role.count())
                .from(role)
                .where(builder)
                .fetchOne();
        return new PageImpl<>(contents, pageable, total);
    }
}
