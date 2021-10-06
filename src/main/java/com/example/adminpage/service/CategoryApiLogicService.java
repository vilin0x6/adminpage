package com.example.adminpage.service;

import com.example.adminpage.ifs.CrudInterface;
import com.example.adminpage.model.entity.Category;
import com.example.adminpage.model.network.Header;
import com.example.adminpage.model.network.request.CategoryApiRequest;
import com.example.adminpage.model.network.response.CategoryApiResponse;
import com.example.adminpage.repository.CategoryRepository;
import com.example.adminpage.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryApiLogicService implements CrudInterface<CategoryApiRequest, CategoryApiResponse> {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public Header<CategoryApiResponse> create(Header<CategoryApiRequest> request) {

        CategoryApiRequest body = request.getData();

        Category category = Category.builder()
                .type(body.getType())
                .title(body.getTitle())
                .build();

        Category newCategory = categoryRepository.save(category);

        return response(newCategory);
    }

    @Override
    public Header<CategoryApiResponse> read(Long id) {
        return categoryRepository.findById(id)
                .map( category -> response(category) )
                .orElseGet( () -> Header.ERROR("데이터 없음") );
    }

    @Override
    public Header<CategoryApiResponse> update(Header<CategoryApiRequest> request) {

        CategoryApiRequest body = request.getData();

        return categoryRepository.findById(body.getId())
                .map(category -> {

                    category
                            .setType(body.getType())
                            .setTitle(body.getTitle())
                            ;

                    return category;

                })
                .map(changeCategory -> categoryRepository.save(changeCategory))
                .map(newCategory -> response(newCategory))
                .orElseGet( () -> Header.ERROR("데이터 없음") );

    }

    @Override
    public Header delete(Long id) {

        return categoryRepository.findById(id)
                .map(category -> {
                    categoryRepository.delete(category);
                    return Header.OK();
                })
                .orElseGet( () -> Header.ERROR("데이터 없음") );

    }

    // 공통 메서드
    private Header<CategoryApiResponse> response(Category category) {

        CategoryApiResponse body = CategoryApiResponse.builder()
                .id(category.getId())
                .type(category.getType())
                .title(category.getTitle())
                .build();

        return Header.OK(body);

    }

}
