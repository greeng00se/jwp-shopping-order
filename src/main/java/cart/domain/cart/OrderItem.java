package cart.domain.cart;

import cart.domain.VO.Money;
import cart.domain.member.Member;
import cart.exception.cart.InvalidCartItemOwnerException;
import java.util.Objects;

public class OrderItem implements Item {

    private final Long id;
    private final Member member;
    private final Product product;
    private Integer quantity;

    public OrderItem(final Long id, final Integer quantity, final Member member, final Product product) {
        this.id = id;
        this.quantity = quantity;
        this.member = member;
        this.product = product;
    }

    @Override
    public Money calculateTotalPrice() {
        return product.getPrice().times(quantity);
    }

    @Override
    public void checkOwner(final Member member) {
        if (!this.member.equals(member)) {
            throw new InvalidCartItemOwnerException();
        }
    }

    @Override
    public void changeQuantity(final int quantity) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public Integer getQuantity() {
        return quantity;
    }

    @Override
    public Member getMember() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Product getProduct() {
        return product;
    }
}
