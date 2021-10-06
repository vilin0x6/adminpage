package com.example.adminpage.service;

import com.example.adminpage.ifs.CrudInterface;
import com.example.adminpage.model.entity.OrderGroup;
import com.example.adminpage.model.entity.User;
import com.example.adminpage.model.enumclass.UserStatus;
import com.example.adminpage.model.network.Header;
import com.example.adminpage.model.network.request.UserApiRequest;
import com.example.adminpage.model.network.response.ItemApiResponse;
import com.example.adminpage.model.network.response.OrderGroupApiResponse;
import com.example.adminpage.model.network.response.UserApiResponse;
import com.example.adminpage.model.network.response.UserOrderInfoApiResponse;
import com.example.adminpage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserApiLogicService implements CrudInterface<UserApiRequest, UserApiResponse> {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderGroupApiLogicService orderGroupApiLogicService;

    @Autowired
    private ItemApiLogicService itemApiLogicService;

    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {

        // 1. request data 가져오기
        UserApiRequest userApiRequest = request.getData();

        // 2. user 생성
        User user = User.builder()
                .account(userApiRequest.getAccount())
                .password(userApiRequest.getPassword())
                .status(UserStatus.REGISTERED)
                .phoneNumber(userApiRequest.getPhoneNumber())
                .email(userApiRequest.getEmail())
                .registeredAt(LocalDateTime.now())
                .build();

        User newUser = userRepository.save(user);

        // 3. 생성된 데이터 기준으로 UserApiResponse return
        return Header.OK(response(newUser));
    }

    @Override
    public Header<UserApiResponse> read(Long id) {

        // id를 통해 데이터 가져오기
        // 해당 데이터 user가 오면 userApiResponse return
        return userRepository.findById(id)
                .map( user -> response(user) )    // user가 있는 경우
                .map(userApiResponse -> Header.OK(userApiResponse))
                .orElseGet( () -> Header.ERROR("데이터 없음") );   // user가 없는 경우

    }

    @Override
    public Header<UserApiResponse> update(Header<UserApiRequest> request) {

        // 1. data 가져오기
        UserApiRequest userApiRequest = request.getData();

        // 2. id로 user 데이터 찾기
        Optional<User> optional = userRepository.findById(userApiRequest.getId());

        return optional.map(user -> {
            user.setAccount(userApiRequest.getAccount())
                    .setPassword(userApiRequest.getPassword())
                    .setStatus(userApiRequest.getStatus())
                    .setPhoneNumber(userApiRequest.getPhoneNumber())
                    .setEmail(userApiRequest.getEmail())
                    .setRegisteredAt(userApiRequest.getRegisteredAt())
                    .setUnregisteredAt(userApiRequest.getUnregisteredAt())
                    ;
            return user;
        })
        .map(user -> userRepository.save(user)) // 3. data update
        .map(user -> response(user))    // 4. userApiResponse 생성
        .map(userApiResponse -> Header.OK(userApiResponse))
        .orElseGet(() -> Header.ERROR("데이터 없음"));

    }

   @Override
    public Header delete(Long id) {
        Optional<User> optional = userRepository.findById(id);

        return optional.map(user -> {
            userRepository.delete(user);
            return Header.OK();
        })
        .orElseGet( ()-> Header.ERROR("데이터 없음"));
    }

    private UserApiResponse response(User user) {
        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword())   // todo 암호화, 길이를 return한다든지 등의 옵션이 생길 수 있음
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .registeredAt(user.getRegisteredAt())
                .unregisteredAt(user.getUnregisteredAt())
                .build();

        return userApiResponse;
    }

    public Header<List<UserApiResponse>> search(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        List<UserApiResponse> userApiResponsesList = users.stream()
                .map(user -> response(user))
                .collect(Collectors.toList());

        return Header.OK(userApiResponsesList);
    }

    public Header<UserOrderInfoApiResponse> orderInfo(Long id) {

        // user
        User user = userRepository.getById(id);
        UserApiResponse userApiResponse = response(user);

        // orderGroup
        List<OrderGroup> orderGroupList = user.getOrderGroupList();
        List<OrderGroupApiResponse> orderGroupApiResponseList = orderGroupList.stream()
                .map(orderGroup -> {
                    OrderGroupApiResponse orderGroupApiResponse = orderGroupApiLogicService.response(orderGroup).getData();

                    // item api response
                    List<ItemApiResponse> itemApiResponseList = orderGroup.getOrderDetailList().stream()
                            .map(detail -> detail.getItem())
                            .map(item -> itemApiLogicService.response(item).getData())
                            .collect(Collectors.toList());

                    orderGroupApiResponse.setItemApiResponseList(itemApiResponseList);
                    return orderGroupApiResponse;
                })
                .collect(Collectors.toList());

        userApiResponse.setOrderGroupApiResponseList((orderGroupApiResponseList));

        UserOrderInfoApiResponse userOrderInfoApiResponse = UserOrderInfoApiResponse.builder()
                .userApiResponse(userApiResponse)
                .build();

        return Header.OK(userOrderInfoApiResponse);
    }
}
