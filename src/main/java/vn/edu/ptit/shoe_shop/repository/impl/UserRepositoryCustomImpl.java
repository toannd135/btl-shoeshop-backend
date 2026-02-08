package vn.edu.ptit.shoe_shop.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import vn.edu.ptit.shoe_shop.common.enums.GenderEnum;
import vn.edu.ptit.shoe_shop.common.enums.StatusEnum;
import vn.edu.ptit.shoe_shop.dto.request.search.UserSearchRequestDTO;
import vn.edu.ptit.shoe_shop.entity.QUser;
import vn.edu.ptit.shoe_shop.entity.User;
import vn.edu.ptit.shoe_shop.repository.UserRepositoryCustom;

import java.time.LocalDate;
import java.util.List;


@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryCustomImpl.class);

    public UserRepositoryCustomImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;

    }

    @Override
    public Page<User> searchUsers(UserSearchRequestDTO request, Pageable pageable) {
        log.debug("Start searchUsers with request: {}", request);
        BooleanBuilder builder = new BooleanBuilder();

        if(StringUtils.hasText(request.getFullName())) {
            log.debug("Filtering by fullName: {}", request.getFullName());
            builder.and(user.firstName.containsIgnoreCase(request.getFullName()))
                    .or(user.lastName.containsIgnoreCase(request.getFullName()));
        }
        if (request.getUsername() != null) {
            log.debug("Filtering by username: {}", request.getUsername());
            builder.and(user.username.containsIgnoreCase(request.getUsername()));
        }
        if (request.getEmail() != null) {
            log.debug("Filtering by email: {}", request.getEmail());
            builder.and(user.email.containsIgnoreCase(request.getEmail()));
        }
        if (request.getPhone() != null) {
            log.debug("Filtering by phone: {}", request.getPhone());
            builder.and(user.phone.containsIgnoreCase(request.getPhone()));
        }
        if (request.getGender() != null) {
            log.debug("Filtering by gender: {}", request.getGender());
            builder.and(user.gender.eq(GenderEnum.valueOf(request.getGender())));
        }
        if (request.getDateOfBirth() != null) {
            log.debug("Filtering by dateOfBirth: {}", request.getDateOfBirth());
            builder.and(user.dateOfBirth.eq(LocalDate.parse(request.getDateOfBirth())));
        }
        if(request.getStatus() != null){
            log.debug("Filtering by status: {}", request.getStatus());
            builder.and(user.status.eq(StatusEnum.valueOf(request.getStatus())));
        }
        log.info("Completed building query conditions");

        JPAQuery<User> query = this.queryFactory
                .selectFrom(user)
                .where(builder);
        log.debug("Constructed base query: {}", query);

        for(Sort.Order order : pageable.getSort()){
            log.debug("Apply sorting: {} {}", order.getProperty(), order.getDirection());
            PathBuilder<User> pathBuilder = new PathBuilder<>(User.class, user.getMetadata());
            query.orderBy(
                    new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(order.getProperty())
                    )
            );
        }

        List<User> contents = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        log.info("Fetched {} users from database", contents.size());
        Long total = this.queryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();
        log.info("Total users matched={}", total);
        return new PageImpl<>(contents, pageable, total);
    }
}
