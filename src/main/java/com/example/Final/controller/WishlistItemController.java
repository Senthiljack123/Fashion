package com.example.Final.controller;

import com.example.Final.Service.WishlistItemService;
import com.example.Final.model.WishlistItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistItemController {
    @Autowired
    private WishlistItemService wishlistItemService;

    @GetMapping("/{userId}")
    public List<WishlistItem> getWishlistItemsByUserId(@PathVariable Long userId) {
        return wishlistItemService.getWishlistItemsByUserId(userId);
    }

    @PostMapping
    public WishlistItem addWishlistItem(@RequestBody WishlistItem wishlistItem) {
        return wishlistItemService.addWishlistItem(wishlistItem);
    }

    @DeleteMapping("/{itemId}")
    public void removeWishlistItem(@PathVariable Long itemId) {
        wishlistItemService.removeWishlistItem(itemId);
    }
}
