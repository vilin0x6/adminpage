package com.example.adminpage.service;

import com.example.adminpage.ifs.CrudInterface;
import com.example.adminpage.model.entity.OrderDetail;
import com.example.adminpage.model.network.Header;
import com.example.adminpage.model.network.request.OrderDetailApiRequest;
import com.example.adminpage.model.network.response.OrderDetailApiResponse;
import com.example.adminpage.repository.ItemRepository;
import com.example.adminpage.repository.OrderDetailRepository;
import com.example.adminpage.repository.OrderGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailApiLogicService implements CrudInterface<OrderDetailApiRequest, OrderDetailApiResponse> {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Header<OrderDetailApiResponse> create(Header<OrderDetailApiRequest> request) {

        OrderDetailApiRequest body = request.getData();

        OrderDetail orderDetail = OrderDetail.builder()
                .status(body.getStatus())
                .arrivalDate(body.getArrivalDate())
                .quantity(body.getQuantity())
                .totalPrice(body.getTotalPrice())
                .orderGroup(orderGroupRepository.getById(body.getOrderGroupId()))
                .item(itemRepository.getById(body.getItemId()))
                .build();

        OrderDetail newOrderDetail = orderDetailRepository.save(orderDetail);

        return response(newOrderDetail);
    }

    @Override
    public Header<OrderDetailApiResponse> read(Long id) {

        return orderDetailRepository.findById(id)
                .map( orderDetail -> response(orderDetail) )
                .orElseGet( () -> Header.ERROR("데이터 없음") );

    }

    @Override
    public Header<OrderDetailApiResponse> update(Header<OrderDetailApiRequest> request) {
        return null;
    }

    @Override
    public Header delete(Long id) {
        return null;
    }

    // 공통 메서드
    private Header<OrderDetailApiResponse> response(OrderDetail orderDetail) {

        OrderDetailApiResponse body = OrderDetailApiResponse.builder()
                .status(orderDetail.getStatus())
                .arrivalDate(orderDetail.getArrivalDate())
                .quantity(orderDetail.getQuantity())
                .totalPrice(orderDetail.getTotalPrice())
                .orderGroupId(orderDetail.getOrderGroup().getId())
                .itemId(orderDetail.getItem().getId())
                .build();

        return Header.OK(body);

    }

}
