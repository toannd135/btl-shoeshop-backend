package vn.edu.ptit.shoe_shop.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.ptit.shoe_shop.common.utils.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final

    @GetMapping("/top-selling-products")
    @ApiMessage("Get top selling products")
    public ResponseEntity<?> getSalesAnalytics() {
        return ResponseEntity.ok().body(null);
    }
}
