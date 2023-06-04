package cart.repository;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import cart.dao.CouponDao;
import cart.dao.MemberCouponDao;
import cart.domain.coupon.Coupon;
import cart.domain.order.MemberCoupon;
import cart.entity.CouponEntity;
import cart.entity.MemberCouponEntity;
import cart.exception.coupon.CouponNotFoundException;
import cart.exception.order.MemberCouponNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.stereotype.Repository;

@Repository
public class MemberCouponRepository {

    private final MemberCouponDao memberCouponDao;
    private final CouponDao couponDao;

    public MemberCouponRepository(final MemberCouponDao memberCouponDao, final CouponDao couponDao) {
        this.memberCouponDao = memberCouponDao;
        this.couponDao = couponDao;
    }

    public void saveAll(final List<MemberCoupon> memberCoupons) {
        final List<MemberCouponEntity> memberCouponEntities = memberCoupons.stream()
                .map(MemberCouponEntity::from)
                .collect(toList());

        memberCouponDao.insertAll(memberCouponEntities);
    }

    public MemberCoupon save(final MemberCoupon memberCoupon) {
        final MemberCouponEntity memberCouponEntity = MemberCouponEntity.from(memberCoupon);
        final MemberCouponEntity savedMemberCouponEntity = memberCouponDao.insert(memberCouponEntity);
        return new MemberCoupon(
                savedMemberCouponEntity.getId(),
                memberCoupon.getMemberId(),
                memberCoupon.getCoupon(),
                memberCoupon.isUsed()
        );
    }

    public Optional<MemberCoupon> findById(final Long id) {
        final Optional<MemberCouponEntity> mayBeMemberCouponEntity = memberCouponDao.findById(id);
        if (mayBeMemberCouponEntity.isEmpty()) {
            return Optional.empty();
        }

        final MemberCouponEntity memberCouponEntity = mayBeMemberCouponEntity.orElseThrow(
                MemberCouponNotFoundException::new);

        final Coupon coupon = couponDao.findById(memberCouponEntity.getCouponId())
                .map(CouponEntity::toDomain)
                .orElseThrow(CouponNotFoundException::new);

        return Optional.of(new MemberCoupon(
                memberCouponEntity.getId(),
                memberCouponEntity.getMemberId(),
                coupon,
                memberCouponEntity.isUsed()
        ));
    }

    public List<MemberCoupon> findAllByMemberId(final Long memberId) {
        final List<MemberCouponEntity> memberCouponEntities = memberCouponDao.findAllByUsedAndMemberId(false, memberId);
        final List<Long> couponIds = memberCouponEntities.stream()
                .map(MemberCouponEntity::getCouponId)
                .collect(toList());
        final Map<Long, Coupon> couponIdByCoupon = couponDao.findByIds(couponIds).stream()
                .map(CouponEntity::toDomain)
                .collect(toMap(Coupon::getId, Function.identity()));
        return memberCouponEntities.stream()
                .map(it -> new MemberCoupon(it.getId(), memberId, couponIdByCoupon.get(it.getCouponId()), it.isUsed()))
                .collect(toList());
    }
}
