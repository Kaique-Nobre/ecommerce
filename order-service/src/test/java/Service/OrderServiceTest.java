package Service;

import com.ecommerce.contracts.event.order.OrderCreatedEvent;
import com.ecommerce.contracts.exception.ResourceNotFoundException;
import com.ecommerce.dtos.request.CreateOrderItemRequest;
import com.ecommerce.dtos.request.CreateOrderRequest;
import com.ecommerce.dtos.response.OrderResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.OrderStatus;
import com.ecommerce.exceptions.EmptyOrderException;
import com.ecommerce.exceptions.InvalidQuantityException;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.menssaging.OrderEventFactory;
import com.ecommerce.menssaging.publisher.DomainEventPublisher;
import com.ecommerce.menssaging.publisher.RabbitDomainEventPublisher;
import com.ecommerce.product.ProductClient;
import com.ecommerce.product.ProductSnapshot;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductClient productClient;

    @Mock
    private RabbitDomainEventPublisher eventPublisher;

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderEventFactory eventFactory;

    @InjectMocks
    private OrderService orderService;

    @Test
    void create_ShouldCreateOrder_WhenSuccessfully() {
        UUID customerId = UUID.randomUUID();

        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(List.of(new CreateOrderItemRequest(productId, 2)));

        ProductSnapshot snapshot =
                new ProductSnapshot(
                        productId,
                        "Notebook",
                        new BigDecimal("5000.00")
                );

        OrderCreatedEvent event =
                new OrderCreatedEvent(
                        UUID.randomUUID(),
                        customerId,
                        BigDecimal.valueOf(10000),
                        List.of()
                );

        OrderResponse response =
                new OrderResponse(
                        UUID.randomUUID(),
                        customerId,
                        OrderStatus.CREATED,
                        BigDecimal.valueOf(10000),
                        OffsetDateTime.now(),
                        List.of()
        );

        when(productClient.getProduct(productId)).thenReturn(snapshot);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(eventFactory.createOrderCreatedEvent(any(Order.class)))
                .thenReturn(event);

        when(mapper.toOrderResponse(any(Order.class)))
                .thenReturn(response);

        OrderResponse result =
                orderService.create(customerId, request);

        assertThat(result).isEqualTo(response);

        verify(productClient).getProduct(productId);

        verify(orderRepository).save(any(Order.class));

        verify(eventFactory).createOrderCreatedEvent(any(Order.class));

        verify(eventPublisher).publish(event);

        verify(mapper).toOrderResponse(any(Order.class));

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);

        verify(orderRepository).save(orderCaptor.capture());

        Order savedOrder = orderCaptor.getValue();

        assertThat(savedOrder.getCustomerId())
                .isEqualTo(customerId);

        assertThat(savedOrder.getItems())
                .hasSize(1);

        OrderItem savedItem =
                savedOrder.getItems().getFirst();

        assertThat(savedItem.getProductId())
                .isEqualTo(productId);

        assertThat(savedItem.getQuantity())
                .isEqualTo(2);

        assertThat(savedItem.getUnitPrice())
                .isEqualByComparingTo("5000.00");

        assertThat(savedOrder.getTotalAmount())
                .isEqualByComparingTo("10000.00");

        InOrder inOrder = inOrder(
                productClient,
                orderRepository,
                eventFactory,
                eventPublisher,
                mapper
        );

        inOrder.verify(productClient)
                .getProduct(productId);

        inOrder.verify(orderRepository)
                .save(any(Order.class));

        inOrder.verify(eventFactory)
                .createOrderCreatedEvent(any(Order.class));

        inOrder.verify(eventPublisher)
                .publish(event);

        inOrder.verify(mapper)
                .toOrderResponse(any(Order.class));
    }

    @Test
    void create_ShouldThrowException_WhenProductDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(List.of(new CreateOrderItemRequest(productId, 2)));

        when(productClient.getProduct(productId))
                .thenThrow(new ResourceNotFoundException("Product was not found"));

        assertThrows(ResourceNotFoundException.class, () -> orderService.create(customerId, request));

        verify(productClient).getProduct(productId);

        verify(orderRepository, never()).save(any(Order.class));

        verify(eventFactory, never()).createOrderCreatedEvent(any(Order.class));

        verify(eventPublisher, never()).publish(any(OrderCreatedEvent.class));

        verify(mapper, never()).toOrderResponse(any(Order.class));
    }

    @Test
    void create_ShouldMergeItems_WhenSameProductIsSentTwice() {

        UUID productId = UUID.randomUUID();

        UUID customerId = UUID.randomUUID();

        CreateOrderRequest request =
                new CreateOrderRequest(
                        List.of(
                                new CreateOrderItemRequest(productId, 1),
                                new CreateOrderItemRequest(productId, 2)
                        )
                );

        ProductSnapshot product =
                new ProductSnapshot(
                        productId,
                        "Notebook",
                        new BigDecimal("5000.00")
                );

        when(productClient.getProduct(productId))
                .thenReturn(product);

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response =
                orderService.create(customerId, request);

        ArgumentCaptor<Order> captor =
                ArgumentCaptor.forClass(Order.class);

        verify(orderRepository)
                .save(captor.capture());

        Order order = captor.getValue();

        assertThat(order.getItems())
                .hasSize(1);

        OrderItem item =
                order.getItems().getFirst();

        assertThat(item.getQuantity())
                .isEqualTo(3);

        assertThat(order.getTotalAmount())
                .isEqualByComparingTo("15000.00");
    }

    @Test
    void create_ShouldThrowException_WhenOrderIsEmpty() {
        UUID customerId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(List.of());

        assertThrows(EmptyOrderException.class, () -> orderService.create(customerId, request));

        verify(productClient, never()).getProduct(any());

        verify(orderRepository, never()).save(any(Order.class));

        verify(eventFactory, never()).createOrderCreatedEvent(any(Order.class));

        verify(eventPublisher, never()).publish(any(OrderCreatedEvent.class));

        verify(mapper, never()).toOrderResponse(any(Order.class));
    }

    @Test
    void create_ShouldThrowException_WhenQuantityIsInvalid() {
        UUID customerId = UUID.randomUUID();

        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(List.of(new CreateOrderItemRequest(productId, 0)));

        ProductSnapshot snapshot =
                new ProductSnapshot(
                        productId,
                        "Notebook",
                        new BigDecimal("5000.00")
                );

        when(productClient.getProduct(productId)).thenReturn(snapshot);

        assertThrows(InvalidQuantityException.class, () -> orderService.create(customerId, request));

        verify(productClient).getProduct(productId);

        verify(orderRepository, never()).save(any(Order.class));

        verify(eventFactory, never()).createOrderCreatedEvent(any(Order.class));

        verify(eventPublisher, never()).publish(any(OrderCreatedEvent.class));

        verify(mapper, never()).toOrderResponse(any(Order.class));
    }

    @Test
    void create_ShouldNotPublishEvent_WhenRepositoryFails() {
        UUID customerId = UUID.randomUUID();

        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(List.of(new CreateOrderItemRequest(productId, 2)));

        ProductSnapshot snapshot =
                new ProductSnapshot(
                        productId,
                        "Notebook",
                        new BigDecimal("5000.00")
                );


        when(productClient.getProduct(productId)).thenReturn(snapshot);

        when(orderRepository.save(any(Order.class)))
                .thenThrow(new DataAccessResourceFailureException("Banco indisponível"));

        assertThrows(
                DataAccessResourceFailureException.class,
                () -> orderService.create(customerId, request)
        );

        verify(eventFactory, never())
                .createOrderCreatedEvent(any());

        verify(eventPublisher, never())
                .publish(any());

        verify(mapper, never())
                .toOrderResponse(any());
    }

}
