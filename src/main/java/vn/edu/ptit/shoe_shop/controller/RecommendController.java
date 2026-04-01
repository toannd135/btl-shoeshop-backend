package vn.edu.ptit.shoe_shop.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;

import vn.edu.ptit.shoe_shop.service.RecommendService;
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/recommend-products")
    @ApiMessage("Get recommended products for the user")
    public ResponseEntity<?> getRecommendProducts() {
        return ResponseEntity.ok().body(this.recommendService.getRecommendProduct());
    }
}
