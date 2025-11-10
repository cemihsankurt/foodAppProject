package com.cemihsankurt.foodAppProject.service;

import com.cemihsankurt.foodAppProject.dto.*;
import com.cemihsankurt.foodAppProject.entity.*;
import com.cemihsankurt.foodAppProject.exception.ResourceNotFoundException;
import com.cemihsankurt.foodAppProject.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService{

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FCMService fcmService;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ICustomerService customerService;


    @Override
    @Transactional
    public OrderDetailsResponseDto createOrderFromCart(Authentication authentication, Long addressId) {

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId()).orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Cart cart = customer.getCart();

        if(cart.getCartItems().isEmpty()){

            throw new IllegalStateException("Cart is empty. Cannot create order.");
        }

        Address deliveryAddress = addressRepository.findById(addressId).orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (!deliveryAddress.getCustomer().getId().equals(customer.getId())) {
            throw new AccessDeniedException("Bu adresi sipariş için kullanma yetkiniz yok.");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.PENDING);

        String addressSnapshot = deliveryAddress.getAddressTitle() + "\n " + deliveryAddress.getFullAddress();
        order.setDeliveryAddress(addressSnapshot);

        BigDecimal cartTotal = BigDecimal.ZERO;
        Restaurant restaurant = null;

        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cart.getCartItems()){

            if(restaurant == null){
                restaurant = cartItem.getProduct().getRestaurant();
                if(!restaurant.isAvailable()){
                    throw new IllegalStateException("Restaurant" + restaurant.getName() + " is not available");
                }
                order.setRestaurant(restaurant);
            }

            Product product = cartItem.getProduct();
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setOrder(order);

            orderItems.add(orderItem);

            cartTotal = cartTotal.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalPrice(cartTotal);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        OrderDetailsResponseDto orderDto = convertToDto(savedOrder);

        Long restaurantId = restaurant.getId();
        String destination = "/topic/orders/restaurant/" + restaurantId;
        messagingTemplate.convertAndSend(destination, orderDto);
        System.out.println("WebSocket notification send" + destination);

        cartItemRepository.deleteAllByCartId(cart.getId());

        return orderDto;


    }

    @Override
    public List<OrderDetailsResponseDto> getOrdersByCustomerId(Authentication authentication) {

        String userEmail = authentication.getName();
        Long customerId = customerRepository.findCustomerIdByUserEmail(userEmail);

        List<Order> orders = orderRepository.findByCustomerId(customerId);
            return orders.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());

    }

    @Override
    public OrderDetailsResponseDto getOrderById(Long orderId, Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean hasPermission = false;

        if (user.getRole() == Role.ROLE_CUSTOMER) {

            Customer customer = customerRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

            if(order.getCustomer().getId().equals(customer.getId())){
                hasPermission = true;
            }
        }else if(user.getRole() == Role.ROLE_RESTAURANT){

            Restaurant restaurant = restaurantRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));

            if(order.getRestaurant().getId().equals(restaurant.getId())){
                hasPermission = true;
            }
        }else if(user.getRole() == Role.ROLE_ADMIN){
            hasPermission = true;
        }
        if(!hasPermission){
            throw new AccessDeniedException("User not permitted to order");
        }

        return convertToDto(order);

    }

    @Override
    public OrderDetailsResponseDto cancelOrder(Long orderId,Authentication authentication) {

        String userEmail = authentication.getName();
        Long customerId = customerService.findCustomerIdByUserEmail(userEmail);


        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));


        if(!order.getCustomer().getId().equals(customerId)){
            throw new AccessDeniedException("Unauthorized access to order");
        }

        if(order.getOrderStatus() != OrderStatus.PENDING){
            throw new IllegalStateException("Only pending orders can be cancelled");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return convertToDto(order);
    }

    @Override
    public List<OrderDetailsResponseDto> getOrdersByRestaurant(Restaurant restaurant) {

        List<Order> orders = orderRepository.findByRestaurantId(restaurant.getId());
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDetailsResponseDto updateOrderStatus(Long orderId, OrderStatus newStatus, Authentication authentication) {

        Restaurant restaurant = getCurrentRestaurant(authentication);
        Order orderToUpdate = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!orderToUpdate.getRestaurant().getId().equals(restaurant.getId())) {
            throw new AccessDeniedException("You have no access to change the status of this order");
        }

        if(orderToUpdate.getOrderStatus() == OrderStatus.COMPLETED || orderToUpdate.getOrderStatus() == OrderStatus.CANCELLED || orderToUpdate.getOrderStatus() == newStatus){
            return convertToDto(orderToUpdate);
        }

        orderToUpdate.setOrderStatus(newStatus);
        Order savedOrder = orderRepository.save(orderToUpdate);

        OrderDetailsResponseDto orderDto = convertToDto(savedOrder);

        Customer customer = savedOrder.getCustomer();

        if (customer.getFcmToken() != null) {
            String title = "Sipariş Durumu Güncellendi!";
            String body = "Restoran  siparişinizin durumunu "
                    + newStatus.toString() + " olarak güncelledi.";

            // Arka planda FCM'e gönder
            fcmService.sendPushNotification(customer.getFcmToken(), title, body);
        }
        return orderDto;

    }

    private Restaurant getCurrentRestaurant(Authentication authentication) {

        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != Role.ROLE_RESTAURANT) {
            throw new AccessDeniedException("Only restaurants can do this action");
        }

        return restaurantRepository.findByUserId(user.getId()).orElseThrow(() -> new AccessDeniedException("This user does not have a restaurant"));
    }


    private OrderDetailsResponseDto convertToDto(Order order) {
        return OrderDetailsResponseDto.builder()
                .orderId(order.getId())
                .restaurantName(order.getRestaurant().getName())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .orderItemDtos(convertOrderItemsToDto(order.getOrderItems()))
                .totalPrice(order.getTotalPrice())
                .orderStatus(order.getOrderStatus())
                .orderTime(order.getOrderTime())
                .build();
    }

    private List<OrderItemDto> convertOrderItemsToDto(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> OrderItemDto.builder()
                        .productName(orderItem.getProductName())
                        .price(orderItem.getPrice())
                        .quantity(orderItem.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }
}
