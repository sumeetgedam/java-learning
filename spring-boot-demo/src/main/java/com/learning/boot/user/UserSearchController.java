package com.learning.boot.user;

import com.learning.boot.common.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserSearchController {

    private final UserSearchService searchService;

    public UserSearchController(UserSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public PageResponse<UserResponse> search(
            @RequestParam(required = false)
            String name,

            @RequestParam(required = false)
            String email,
            Pageable pageable
    ) {
        Page<UserResponse> page =
                searchService.search(
                        name,
                        email,
                        pageable
                );

        return PageResponse.from(page);
    }


}
